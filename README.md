# cordova-plugin-unzip
Unzip Cordova plugin

## Installation
```bash
cordova plugin add @globules-io/cordova-plugin-unzip
cordova plugin rm @globules-io/cordova-plugin-unzip
```
## Supported Platforms
> Android<br>
> iOS

## JS API
```js
let source = cordova.file.dataDirectory + 'myZipFile.zip';
let dest = cordova.file.dataDirectory + 'myFolder'
Unzip.unpack(source, dest, 
     ()=>{
          console.log('SUCCESS');
     },
     (error)=>{
          console.log('ERROR', error);
     }
);
```