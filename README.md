Reachability on Android
============
Easy access on top.  
Like a iPhone 6.

---

## Usage
add dependencies
 * __Not yet!! Please wait a moment now.__

~~compile 'com.github.sakebook:Reachability:0.0.1@aar'~~

In Activity `onCreate`

```
Reachability reachability = new Reachability(this);
reachability.makeHoverView(Reachability.Position.RIGHT);
```

## Option
### Show status bar
 * canTouchableBackView

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
 * setHoverView
 * setCustomSlideInAnimation
 * setCustomSlideOutAnimation

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
