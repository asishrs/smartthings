# SmartThings - Ring Alarm Device & Smart-App

> :mega: This is the `version 3 `of this application. If you are looking for older version, visit https://github.com/asishrs/smartthings-ringalarm

This repository contains device handler and smart-app for Ring Alarm integration with SmartThings. In order to use this, you need to deploy the API part as described [here](https://github.com/asishrs/smartthings-ringalarmv2 ).

:warning: [April 5 2020] Due to the recent changes in the Ring 2FA changes, this application was updated to support the 2FA use cases. Unfortunately, this means the support for the accounts without 2FA is no more actively supported. Please update your Ring Account with 2FA support to use this.

## Prerequisites 
[Ring Alarm API](https://github.com/asishrs/smartthings-ringalarmv2 ) 

 :warning:You need to have a working API before proceed to the next steps.

## Supported Ring Devices

- Alarm Base
- Keypad
- Motion Sensor
- Contact Sensor
- Z-Wave Range Extender 
- Flood Freeze
- Smoke/CO Listener

## Installation

### GitHub Integration (Recommended Method)
It is highly recommended that you use the GitHub Integration that SmartThings offers with their IDE. This will make it much easier for you to keep up to date with changes over time. For the general steps needed for setting up GitHub IDE integration, please visit [this](http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html) and follow the steps for performing the setup.

If this is your first time installing this, first follow these steps to link your IDE with the code repository. 
1. Login to the IDE at ide.smartthings.com
1. Click on My Locations at the top of the page
1. Click on the name of the location that you want to install to
1. Click on the **My Device Handlers** tab
1. Click **Settings**
1. Click **Add new repository** and use the following parameters:
    1. Owner: asishrs
    1. Name: smartthings
    1. Branch: master
1. Click **Save**

#### Installing Device Handlers
Once the repository is connected to your IDE, use the GitHub integration to install the current version into your workspace. In the IDE:

1. Click **Update from Repo** and select the `smartthings` repository you just added
1. Find and Select below device handlers Select Publish(bottom right of screen near the Cancel button)
    1. `ring-alarm-contact-sensor.groovy`
    1. `ring-alarm-floodfreeze-sensor.groovy`
    1. `ring-alarm-motion-sensor.groovy`
    1. `ring-alarm.groovy`
1. Click **Execute Update**

Note the response at the top of the My Devices Handlers page. It should be something like "Updated 0 devices and created 4 new devices, 4 published"
Verify that the four devices show up in the list and are marked with Status Published (NOTE: You may have to reload the My Device Handlers screen for the devices to show up properly.)

#### Installing SmartApps
Once you have the Device Handlers added and published in your IDE, it is time to add the SmartApps.

1. Click on the **My SmartApps** tab
1. Click **Update from Repo** and select the `smartthings` repository you added earlier
1. Select the check boxes next to `ring-alarm-manager.groovy`
1. Select **Publish**(bottom right of screen near the Cancel button)
1. Click **Execute Update**

Again, note the response at the top of the My SmartApps page. It should be something like "Updated 0 and created 1 SmartApps, 1 published"

### Manual Install 

#### SmartThings Device Handler
 1. Login at http://graph.api.smartthings.com
 1. Select **My Locations**, select the location you want to use.
 1. Select  **My Device Handlers**
 1. Click on the **+ New Device Handler** button on the right.
 1. On the **New Device Handler** page, Select the Tab **From Code**
 1. Copy the [ring-alarm-contact-sensor.groovy](devicetypes/asishrs/ring-alarm-contact-sensor.src/ring-alarm-contact-sensor.groovy) source code and paste it into the IDE editor window.
 1. Click the **Create** button at the bottom.
 1. Click the blue **Save** button above the editor window.
 1. Click the **Publish** button next to it and select **For Me**. You have now self-published your Device Handler
 1. Repeat step 1 to 9 for below devices as well.
    1. [ring-alarm-floodfreeze-sensor.groovy](devicetypes/asishrs/ring-alarm-floodfreeze-sensor.src/ring-alarm-floodfreeze-sensor.groovy)
    1. [ring-alarm-motion-sensor.groovy](devicetypes/asishrs/ring-alarm-motion-sensor.src/ring-alarm-motion-sensor.groovy)
    1. [ring-alarm.groovy](devicetypes/asishrs/ring-alarm.src/ring-alarm.groovy)

#### SmartThings App
1. *(optional)* Login at http://graph.api.smartthings.com
1. *(optional)* Select **My Locations**, select the location you want to use.
1. Select **My SmartApps**
1. Click on the **+ New SmartApp** button on the right.
1. On the **New SmartApp**  page, Select the Tab **From Code**
1. Copy the [ring-alarm-manager.groovy](smartapps/asishrs/ring-alarm.src/ring-alarm-manager.groovy) source code and paste it into the IDE editor window
1. Click the **Create** button at the bottom.
1. Click the blue **Save** button above the editor window.
1. Click the **Publish** button next to it and select **For Me**. You have now self-published your SmartApp

## Setup Alarm Device  
  1. (optional) Login at http://graph.api.smartthings.com
  1. (optional) Select My Locations, select the location you want to use.
  1. Select **My Devices**
  1. Click on the **+ New Device** button on the right.
  1. Fill the Name and Network ID Field (can be anything you like)
  1. Under Type, select **Ring Alarm**
  1. Select appropriate options under Location and Hub
  1. Click **Create**

## Setup SmartThings App
This is based on *SmartThing Classic App*.

1. Open your SmartThings app and go to **Marketplace**
1. Tap on the **SmartApps** and then scroll to the bottom of the page and tap on **My Apps**
1. Tap on **Ring Alarm Manager**
1. Configuration
    1. AWS API for SmartThings Integration
        1. Pause AWS Ring Integration - Choose that only of you want to temporarily disable the integration.
        1. API Url - AWS API URL for Ring Alarm Lambda
        1. API Key -  AWS API Key for Ring Alarm Lambda
        1. Polling Interval - Choose the desired interval, default 5 minutes
    1. Ring Account
        1. Ring Two Factor Enabled - Select this if you have enabled Two factor Authentication in Ring Account.
        1. Do you have an existing Refresh Key - If you already have a refresh key for Ring Two Enabled Account, you can select this. You will be asked to provide that in the next step.
        1. Ring user name - Ring user email address
        1. Ring password - Ring account password.
        
        If you said `YES` to step 2.1, proceed as below.

        1. Tap on **Finalize the Ring Account Setup**.
        1. If you have selected yes to step 2.1, you will be receiving a Text message from Ring on your registered mobile. Enter the text message on **2FA Code** 
        1.  Tap on **Account Status** to see the Ring Account Connection Status. If the connection is successful, you can see the ZID and Location.

        If you said `NO` to step 2.1, proceed as below.
        
        1.  Tap on **Account Status** to see the Ring Account Connection Status. If the connection is successful, you can see the ZID and Location.

    1. SmartThings Alarm Device
        1. Select the Alarm Device you installed. This is mandatory information otherwise the SmartApp cannot refresh the Alarm and Sensor status.
        1. You choose the next three options based on how do you want the alarm to behave for SmartThings mode changes. This is not same as SHM mode.  

    1. SmartThings Notifications
        1. You can choose the notification options as well as a custom name on this.  

## Update to latest version

Read the [release notes](RELEASE.md)

## Support

**Do you have more devices?**

Either open an issue with the device type, or you can make a PR with the required changes. 

**Other issues?**
Please open an issue.

## License

SmartThings - Ring Alarm Device & Smart-App is released under the [MIT License](https://opensource.org/licenses/MIT).