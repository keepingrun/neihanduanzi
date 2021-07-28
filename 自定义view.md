#### MeasureSpec.UNSPECIFIED：

尽可能的大，ListView、ScrollView，在测量子布局的时候会用UNSPECIFIED。



父view的MeasureMode会传递给子view。



设置宽高：

setMeasuredDimension(int measuredWidth, int measuredHeight)；