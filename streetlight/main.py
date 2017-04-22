# streetlight
# Example for the code of an IoT device that is still in development but must be tested.
# This is the SUT - System Under Test

import RPi.GPIO as GPIO
import paho.mqtt.client as mqtt
import json

## CONSTANTS ##################################################################

MQTT_HOST = "10.10.10.1"
STREETLIGHT_ID = "1"
TOPIC_STREETLIGHT_COMMAND = "scyv/smartcity/streetlights/cmd"
TOPIC_STREETLIGHT_STATUS = "scyv/smartcity/streetlights/status"

CHANNEL_IN_LIGHT_SENSOR = 17
CHANNEL_OUT_LIGHT_SWITCH = 23

## METHODS ####################################################################


def switch_light_on(cmdId):
    GPIO.output(CHANNEL_OUT_LIGHT_SWITCH, GPIO.HIGH)
    status = {
        "streetlightId": STREETLIGHT_ID,
        "light": "on",
        "cmdId": cmdId
    }
    mqtt_client.publish(TOPIC_STREETLIGHT_STATUS + "/" + STREETLIGHT_ID, json.dumps(status))
    mqtt_client.publish(TOPIC_STREETLIGHT_STATUS, json.dumps(status))


def switch_light_off(cmdId):
    GPIO.output(CHANNEL_OUT_LIGHT_SWITCH, GPIO.LOW)
    status = {
        "streetlightId": STREETLIGHT_ID,
        "light": "off",
        "cmdId": cmdId
    }
    mqtt_client.publish(TOPIC_STREETLIGHT_STATUS + "/" + STREETLIGHT_ID, json.dumps(status))
    mqtt_client.publish(TOPIC_STREETLIGHT_STATUS, json.dumps(status))

## SETUP MQTT CLIENT ##########################################################


# The callback for when the client receives a CONNACK response from the server.
def on_mqtt_connect(client, userdata, flags, rc):
    print("Connected to MQTT with result code " + str(rc))
    # listen for commands coming from the mqtt bus
    client.subscribe(TOPIC_STREETLIGHT_COMMAND + "/" + STREETLIGHT_ID)


# The callback for when a PUBLISH message is received from the server.
def on_mqtt_message(client, userdata, msg):
    payload_json = json.loads(msg.payload.decode('ascii'))

    if payload_json['light'] == "on":
        switch_light_on(payload_json["cmdId"])

    if payload_json['light'] == "off":
        switch_light_off(payload_json["cmdId"])


# Create an MQTT client and attach our routines to it.
mqtt_client = mqtt.Client()
mqtt_client.on_connect = on_mqtt_connect
mqtt_client.on_message = on_mqtt_message


## SETUP GPIOs ################################################################

GPIO.setmode(GPIO.BCM)

# inputs
GPIO.setup(CHANNEL_IN_LIGHT_SENSOR, GPIO.IN)

#outputs
GPIO.setup(CHANNEL_OUT_LIGHT_SWITCH, GPIO.OUT)


def init_gpios():
    light_sensor_state = GPIO.input(CHANNEL_IN_LIGHT_SENSOR)
    if (light_sensor_state == GPIO.HIGH):
        GPIO.output(CHANNEL_OUT_LIGHT_SWITCH, GPIO.LOW)
    else:
        GPIO.output(CHANNEL_OUT_LIGHT_SWITCH, GPIO.HIGH)


def on_light_sensor_low():
    print("It's getting dark => Switching on the light")
    switch_light_on("")


def on_light_sensor_high():
    print("It's dawn => Switching off the light")
    switch_light_off("")

init_gpios()

# detect falling edge on light sensor (falling means: It's dark)
GPIO.add_event_detect(CHANNEL_IN_LIGHT_SENSOR, GPIO.FALLING, callback=on_light_sensor_low, bouncetime=200)

# detect rising edge on light sensor (rising means: It's bright outside)
GPIO.add_event_detect(CHANNEL_IN_LIGHT_SENSOR, GPIO.RISING, callback=on_light_sensor_high, bouncetime=200)


mqtt_client.connect(MQTT_HOST, 1883, 60)

try:
    # Process network traffic and dispatch callbacks.
    mqtt_client.loop_forever()
except KeyboardInterrupt:
    pass

GPIO.cleanup()
