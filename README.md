今天分享一个以前实现的通讯录字母导航控件，下面自定义一个类似通讯录的字母导航 View，可以知道需要自定义的几个要素，如绘制字母指示器、绘制文字、触摸监听、坐标计算等，自定义完成之后能够达到的功能如下：


- 完成列表数据与字母之间的相互联动;
- 支持布局文件属性配置;
- 在布局文件中能够配置相关属性，如字母颜色、字母字体大小、字母指示器颜色等属性;

主要内容如下：

1. 自定义属性
2. Measure测量
3. 坐标计算
4. 绘制
5. 显示效果


#### 自定义属性

在 value 下面创建 attr.xml ，在里面配置需要自定义的属性，具体如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="LetterView">
        <!--字母颜色-->
        <attr name="letterTextColor" format="color" />
        <!--字母字体大小-->
        <attr name="letterTextSize" format="dimension" />
        <!--整体背景-->
        <attr name="letterTextBackgroundColor" format="color" />
        <!--是否启用指示器-->
        <attr name="letterEnableIndicator" format="boolean" />
        <!--指示器颜色-->
        <attr name="letterIndicatorColor" format="color" />
    </declare-styleable>
</resources>
```

然后在相应的构造方法中获取这些属性并进行相关属性的设置，具体如下：


```java
public LetterView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    //获取属性
    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LetterView);
    int letterTextColor = array.getColor(R.styleable.LetterView_letterTextColor, Color.RED);
    int letterTextBackgroundColor = array.getColor(R.styleable.LetterView_letterTextBackgroundColor, Color.WHITE);
    int letterIndicatorColor = array.getColor(R.styleable.LetterView_letterIndicatorColor, Color.parseColor("#333333"));
    float letterTextSize = array.getDimension(R.styleable.LetterView_letterTextSize, 12);
    enableIndicator = array.getBoolean(R.styleable.LetterView_letterEnableIndicator, true);

    //默认设置
    mContext = context;
    mLetterPaint = new Paint();
    mLetterPaint.setTextSize(letterTextSize);
    mLetterPaint.setColor(letterTextColor);
    mLetterPaint.setAntiAlias(true);

    mLetterIndicatorPaint = new Paint();
    mLetterIndicatorPaint.setStyle(Paint.Style.FILL);
    mLetterIndicatorPaint.setColor(letterIndicatorColor);
    mLetterIndicatorPaint.setAntiAlias(true);

    setBackgroundColor(letterTextBackgroundColor);

    array.recycle();
}
```

#### Measure测量

要想精确的控制自定义的尺寸以及坐标，必须要测量出当前自定义 View 的宽高，然后才可以通过测量到的尺寸计算相关坐标，具体测量过程就是继承 View 重写 omMeasure() 方法完成测量，关键代码如下：


```java
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    //获取宽高的尺寸大小
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    //wrap_content默认宽高
    @SuppressLint("DrawAllocation") Rect mRect = new Rect();
    mLetterPaint.getTextBounds("A", 0, 1, mRect);
    mWidth = mRect.width() + dpToPx(mContext, 12);
    int mHeight = (mRect.height() + dpToPx(mContext, 5)) * letters.length;

    if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT &&
            getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
        setMeasuredDimension(mWidth, mHeight);
    } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
        setMeasuredDimension(mWidth, heightSize);
    } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
        setMeasuredDimension(widthSize, mHeight);
    }

    mWidth = getMeasuredWidth();
    int averageItemHeight = getMeasuredHeight() / 28;
    int mOffset = averageItemHeight / 30; //界面调整
    mItemHeight = averageItemHeight + mOffset;
}
```

#### 坐标计算

自定义 View 实际上就是在 View 上找到合适的位置，将自定义的元素有序的绘制出来即可，绘制过程最困难的就是如何根据具体需求计算合适的左边，至于绘制都是 API 的调用，只要坐标位置计算好了，自定义 View 绘制这一块应该就没有问题了，下面的图示主要是标注了字母指示器绘制的中心位置坐标的计算以及文字绘制的起点位置计算，绘制过程中要保证文字在指示器中心位置，参考如下：


![自定义字母导航](https://upload-images.jianshu.io/upload_images/2494569-470262b0f219a249.PNG?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 绘制

自定义 View 的绘制操作都是在 onDraw() 方法中进行的，这里主要使用到圆的绘制以及文字的绘制，具体就是 drawCircle() 和 drawText() 方法的使用，为避免文字被遮挡，需现绘制字母指示器，然后再绘制字母，代码参考如下：

```java
@Override
protected void onDraw(Canvas canvas) {
    //获取字母宽高
    @SuppressLint("DrawAllocation") Rect rect = new Rect();
    mLetterPaint.getTextBounds("A", 0, 1, rect);
    int letterWidth = rect.width();
    int letterHeight = rect.height();

    //绘制指示器
    if (enableIndicator){
        for (int i = 1; i < letters.length + 1; i++) {
            if (mTouchIndex == i) {
                canvas.drawCircle(0.5f * mWidth, i * mItemHeight - 0.5f * mItemHeight, 0.5f * mItemHeight, mLetterIndicatorPaint);
            }
        }
    }
    //绘制字母
    for (int i = 1; i < letters.length + 1; i++) {
        canvas.drawText(letters[i - 1], (mWidth - letterWidth) / 2, mItemHeight * i - 0.5f * mItemHeight + letterHeight / 2, mLetterPaint);
    }
}
```

到此为止，可以说 View 的基本绘制结束了，现在使用自定义的 View 界面能够显示出来了，只是还没有添加相关的事件操作，下面将在 View 的触摸事件里实现相关逻辑。

#### Touch事件处理

为了判断手指当前所在位置对应的是哪一个字母，需要获取当前触摸的坐标位置来计算字母索引，重新 onTouchEvent() 方法，监听 MotionEvent.ACTION_DOWN、MotionEvent.ACTION_MOVE 来计算索引位置，监听 MotionEvent.ACTION_UP 将获得结果回调出去，具体参考如下：


```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
            isTouch = true;
            int y = (int) event.getY();
            Log.i("onTouchEvent","--y->" + y + "-y-dp-->" + DensityUtil.px2dp(getContext(), y));
            int index = y / mItemHeight;
            
            if (index != mTouchIndex && index < 28 && index > 0) {
                mTouchIndex = index;
                Log.i("onTouchEvent","--mTouchIndex->" + mTouchIndex + "--position->" + mTouchIndex);
            }

            if (mOnLetterChangeListener != null && mTouchIndex > 0) {
                mOnLetterChangeListener.onLetterListener(letters[mTouchIndex - 1]);
            }

            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isTouch = false;
            if (mOnLetterChangeListener != null && mTouchIndex > 0) {
                mOnLetterChangeListener.onLetterDismissListener();
            }
            break;
    }
    return true;
}
```

到此为止，View 的自定义关键部分基本完成。

#### 数据组装

字母导航的基本思路是将某个需要与字母匹配的字段转换为对应的字母，然后按照该字段对数据进行排序，最终使得通过某个数据字段的首字母就可以批匹配到相同首字母的数据了，这里将汉字转化为拼音使用的是 pinyin4j-2.5.0.jar ，然后对数据项按照首字母进行排序将数据展示到出来即可，汉字装换为拼音如下：


```java

//汉字转换为拼音
public static String getChineseToPinyin(String chinese) {
    StringBuilder builder = new StringBuilder();
    HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
    format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
    format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

    char[] charArray = chinese.toCharArray();
    for (char aCharArray : charArray) {
        if (Character.isSpaceChar(aCharArray)) {
            continue;
        }
        try {
            String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(aCharArray, format);
            if (pinyinArr != null) {
                builder.append(pinyinArr[0]);
            } else {
                builder.append(aCharArray);
            }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
            builder.append(aCharArray);
        }
    }
    return builder.toString();
}
```

至于数据排序使用 Comparator 接口即可，这里就不在赘述了，具体获取文末源码链接查看。


#### 显示效果

显示效果如下：

![](https://github.com/jzmanu/MLetterView/blob/master/screenshot/letterView.gif?raw=true)

在公众号回复关键字【MLetterView】查看源码。