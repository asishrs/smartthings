# Ring Alarm Release

## Release v 3.3.0

### Features/Fixes

- Support for Ring refresh token authentication. [Issue 40](https://github.com/asishrs/smartthings-ringalarmv2/issues/40)
- Ability to reset Ring Token
- Ability to view Location details.
- Ability to view Tokens
- UI Enhancements 
- Updated the name of `off` mode to `disarmed`. [Issue 3](https://github.com/asishrs/smartthings-ringalarmv2/issues/3)

### Installation

- Pause the SmartThings API integration. Open the _Ring Alarm Manager_ > _AWS API for SmartThings Integration_ > Toggle Button on for _Pause AWS Ring API Integration..._
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