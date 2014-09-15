Reachability on Android
============
Easy access on top.  
Like a iPhone 6 & 6 Plus.

![image](https://raw.githubusercontent.com/sakebook/Reachability/master/images/demo.gif)
---

## Usage
Add dependencies

```
compile 'com.github.sakebook:Reachability:0.0.1@aar'
```

In Activity `onCreate`

```
Reachability reachability = new Reachability(this);
reachability.makeHoverView(Reachability.Position.RIGHT);
```

## Option

### Use own trigger  
 * `switchBack`
  * If you call this method, allows you to move the screen.
  * Animation does not overlap.
 * `switchHover`
  * If you call this method, allows you to move the Hover.
  * Animation does not overlap.

### Show status bar
 * `canTouchableBackView`
  * if you call this method, You must write the AndroidManifest.xml the following code.

```AndroidManifest.xml
...
<uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />
...
```

```
reachability.canTouchableBackView(true);
```

## Custom
### HoverView custom
 * `setHoverView`
 * `setCustomSlideInAnimation`
 * `setCustomSlideOutAnimation`

```
// Make Own HoverView. Support only ImageView.
ImageView view = new ImageView(this);
view.setBackgroundResource(R.drawable.custom_button_selector);
view.setScaleType(ImageView.ScaleType.CENTER);
...
mReachability = new Reachability(this);
// Should call before makeHoverView!
mReachability.setHoverView(view, android.R.drawable.ic_partial_secure, android.R.drawable.ic_secure);
mReachability.makeHoverView(Reachability.Position.CENTER);
mReachability.setCustomSlideInAnimation(1000, new AnticipateOvershootInterpolator(), fromLeftAnimation());
mReachability.setCustomSlideOutAnimation(1000, new AnticipateOvershootInterpolator(), toRightAnimation());
```

Sample in project [demo](https://github.com/sakebook/Reachability/tree/master/demo)

## LICENSE
```
Copyright (C) 2014 Shinya Sakemoto

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
