# testbox
# Example for the code of the textbox that will execute the tests for our streetlight
# This is the SUT - System Under Test

import RPi.GPIO as GPIO
import paho.mqtt.client as mqtt
import json

streetlight_id = "1"
mqtt_server = "192.168.10.24"

#GPIO.setmode(GPIO.BOARD)

#def my_callback(channel):
#    print('This is a edge event callback function!')
#    print('Edge detected on channel %s'%channel)
#    print('This is run in a different thread to your main program')

#GPIO.add_event_detect(channel, GPIO.RISING, callback=my_callback)  # add rising edge detection on a channel
#...the rest of your program...


# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):

    print("Connected with result code " + str(rc))

    # subscribe to topics for streetlight control
    client.subscribe("scyv/smartcity/streetlights/" + streetlight_id)
    client.subscribe("scyv/smartcity/streetlights")

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):

    print(msg.topic+" "+str(msg.payload))
    print(msg.payload)
    parsed_json = json.loads(msg.payload.decode('ascii'))

    if parsed_json['switch'] == "Hello":
        print("Received message #1, do something")
        # Do something

    if parsed_json['switch'] == "World!":
        print("Received message #2, do something else")
        # Do something else

# Create an MQTT client and attach our routines to it.
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect(mqtt_server, 1883, 60)

# Process network traffic and dispatch callbacks.
client.loop_forever()
