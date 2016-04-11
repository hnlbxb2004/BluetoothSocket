# BluetoothSocket
 
 此library 库可以方便的帮你使用经典蓝牙做socket 通讯。
 网上的一些蓝牙游戏互联都是用的经典蓝牙做socket  ，
 
 
# 使用
###service端 启动，等待被连接


```java

BluetoothSocketHelper mHelper = new BluetoothSocketHelper();

mHelper.strat();


```
### Client 端，连接设

```java

BluetoothSocketHelper mHelper = new BluetoothSocketHelper();
mHelper.connect(mBluetoothDevice);
	
```


### service 和 client 发送消息
```java
 mHelper.write("要写入的消息");

```


###停止
```java
 mHelper.stop();

```

## License
The MIT License (MIT) Copyright (c) 2016 hnlbxb2004



Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
