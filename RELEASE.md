# Ring Alarm Release

## Release v3.4.0

### Features/Fixes

- Changed the Ring Token Refresh UI.
- Ability to print Device Logs in SmartThings and AWS lambda logs.
- Advanced Settings section for Token Refresh and Device Logs.

### Installation

If you are updating from v3.3.0, follow below.
- Deploy new Ring AWS Lambda.
  - Download the deployment.zip from [Release v3.4.0](https://github.com/asishrs/smartthings-ringalarmv2/releases/tag/v3.3.0)
  - Login to AWS Lambda https://console.aws.amazon.com/lambda/home?region=us-east-1#/functions 
  - Select your lambda for Ring Integration.
  - Confirm the **Code entry type** as _Upload a .zip file_
  - Click on **Upload** button, choose the _deployment.zip_ file downloaded before.
  - Click on **Save** 
- Update Ring Alarm SmartThings App. Follow instruction at https://github.com/asishrs/smartthings#installing-smartapps
- Open the Ring Alarm SmartThings App and check the changes

If you are updating from older versions, remove the Ring Alarm SmartThings App before following steps above.


## Release v3.3.0

### Features/Fixes

- Support for Ring refresh token authentication. [Issue 40](https://github.com/asishrs/smartthings-ringalarmv2/issues/40)
- Ability to reset Ring Token
- Ability to view Location details.
- Ability to view Tokens
- UI Enhancements 
- Updated the name of `off` mode to `disarmed`. [Issue 3](https://github.com/asishrs/smartthings-ringalarmv2/issues/3)

### Installation

- Remove the Ring Alarm SmartThings App
- Deploy new Ring AWS Lambda.
  - Download the deployment.zip from [Release v3.30](https://github.com/asishrs/smartthings-ringalarmv2/releases/tag/v3.3.0)
  - Login to AWS Lambda https://console.aws.amazon.com/lambda/home?region=us-east-1#/functions 
  - Select your lambda for Ring Integration.
  - Confirm the **Code entry type** as _Upload a .zip file_
  - Click on **Upload** button, choose the _deployment.zip_ file downloaded before.
  - Click on **Save** 
- Update Ring Alarm SmartThings Device Handler. Follow instruction at https://github.com/asishrs/smartthings#installing-device-handlers
- Update Ring Alarm SmartThings App. Follow instruction at https://github.com/asishrs/smartthings#installing-smartapps
- Delete the Ring Alarm SmartThings App and reinstall it. 