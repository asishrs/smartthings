# SmartThings - Ring Alarm Device & Smart-App

<aside class="notice">

This is the `version 2 `of this application. If you are looking for the `version 1`, visit https://github.com/asishrs/smartthings-ringalarm

</aside>

This repository contains device handler and smart-app for Ring Alarm integration with SmartThings. In order to use this, you need to deploy the API part as described [here](https://github.com/asishrs/smartthings-ringalarmv2 ).

## Prerequisites 
[Ring Alarm API](https://github.com/asishrs/smartthings-ringalarmv2 ) 

<aside class="warning">

You need to have a working API before proceed to the next steps.

</aside>

## Supported Ring Devices

- Alarm Base
- Keypad
- Motion Sensor
- Contact Sensor
- Z-Wave Range Extender 


### Manual Install SmartThings Device Handler
 - Login at http://graph.api.smartthings.com
 - Select **My Locations**, select the location you want to use.
 - Select  **My Device Handlers**
 - Click on the **+ New Device Handler** button on the right.
 - On the **New Device Handler** page, Select the Tab **From Code**
  - Copy the [ring-alarm-device-handler.groovy](smartthings/ring-alarm-device-handler.groovy) source code and paste it into the IDE editor window.
  - Click the **Create** button at the bottom.
  - Click the blue **Save** button above the editor window.
  - Click the **Publish** button next to it and select **For Me**. You have now self-published your Device Handler

<aside class="notice">

Update the number of ring devices in the code, check for below part.

```
//Define number of devices here.
def motionSensorCount = 5
def contactSensorCount = 6
def rangeExtenderCount = 1
def keypadCount = 1
```

</aside>

### Install SmartThings App
 - *(optional)* Login at http://graph.api.smartthings.com
 - *(optional)* Select **My Locations**, select the location you want to use.
 - Select **My SmartApps**
- Click on the **+ New SmartApp** button on the right.
- On the **New SmartApp**  page, Select the Tab **From Code**
- Copy the [ring-alarm-app.groovy](smartthings/ring-alarm-app.groovy) source code and paste it into the IDE editor window
- Click the **Create** button at the bottom.
- Click the blue **Save** button above the editor window.
- Click the **Publish** button next to it and select **For Me**. You have now self-published your SmartApp

## Setup your SmartThings App
This is based on *Smarthing Classic App*.

- Open your SmartThings app and go to **My Home**
- Tap on the Ring Alarm and then tap on the **settings** (*gear icon*).
- Add below
  - **Ring User Name**
  - **Ring Password**
  - **API Url** - Invoke URL from Lambda setup
  - **API Key** - API key from Lambda setup
  - **Location Id** - Location Id value found in browser Network panel.
  - **ZID** - ZID value found in browser Network panel.
  - **Polling Interval** - Polling interval between Ring Status API call.

|                           My Home                            | Ring Alarm Settings                                          |
| :----------------------------------------------------------: | ------------------------------------------------------------ |
| ![SmartThings - My Home](images/smarthings_classic_app.jpg?raw=true "SmartThings Classic- Home") | ![SmartThings - My Home](images/smartthings-classic-app-settings.jpg?raw=true "SmartThings Classic- Home") |

## Support

**Do you have more devices?**

Either open an issue with the device type, or you can make a PR with the required changes. 

**Other issues?**
Please open an issue.

## License

SmartThings - Ring Alarm Device & Smart-App is released under the [MIT License](https://opensource.org/licenses/MIT).