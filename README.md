Page-Debugging
==============
## Summary
Current site pages are always divided into many nested page fragments. These page fragments improved code re-usability, but meantime made it more difficult for a web developer to find the right fragment file to modify. This tool can make the finding easy.

## Features
This tool’s functions can be simply described as follows:  

###Before using the tool,
 ![before using page-debugging](https://github.com/tminglei/page-debugging/raw/master/doc/before-using-page-debugging.png)

###After using the tool _(i.e. append `pageDebugging=on` to current url)_,
 ![after using page-debugging](https://github.com/tminglei/page-debugging/raw/master/doc/after-using-page-debugging.png)

As you see, the included files are traced with `<!—[_debugging code_] - start -->` and `<!-- [_debugging code_] - end -->`, so you can easily find the right fragment files. 

> p.s. debugging code format is: `‘L’ + [level number] + ‘#’ + [sequence number] + ‘:’ + [file name]`, so you can get these info from it:  
> > a) The path name of an included file  
> > b) Begin and end points of an included file  
> > c) Level of an included file in current page structure

## Technical Principles
Use the aspect technology to insert a HTML comment (tracking code) at begin and end of the output content when the web container or template engine includes a page fragment.

> Supports Tomcat 6.0+, JBoss-Web 2, Freemarker 2+ and Velocity 1.5/1.7

## Installation & Configuration
####1. Let AspactJ have a chance to intercept JVM's class loading
> a) Ensure [aspectjweaver.jar](https://github.com/tminglei/page-debugging/raw/master/dist/aspectjweaver.jar) is on your machine;  
> b) Add JVM argument: `-javaagent:[pathto]\aspectjweaver.jar`  

If you're using eclipse with the tomcat plugin, you can do it like this:  
 ![configure jvm arguments in eclipse](https://github.com/tminglei/page-debugging/raw/master/doc/configure-jvm-arguments-in-eclipse.png)

####2. setup tomcat and your application
> a) Place [page-debugging.jar](https://github.com/tminglei/page-debugging/raw/master/dist/page-debugging-1.5.3.jar) and [aspectjrt.jar](https://github.com/tminglei/page-debugging/raw/master/dist/aspectjrt.jar) under dir `[tomcat home]\lib`  
> b) Configure a customized class loader for your app on tomcat's server.xml, to ensure some page-debugging-tool classes used by app indirectly can be loaded with the same class loader as your app from `page-debugging.jar`;  
> c) Add a listener class (a tomcat valve) to runtime environment, so you can turn on/off this tool as you expected:  

 ![configure tomcat server.xml (lite)](https://github.com/tminglei/page-debugging/raw/master/doc/configure-tomcat-server-xml.png)

> valve: `tml.pagedebugging.PageDebuggingValve`  
> class loader: `tml.pagedebugging.classloader.BridgedClassLoader`  

####3. Then， you can turn on/off this tool by appending `pageDebugging=true/on` or `pageDebugging=false/off` to current page URL. 
> ps: switch status info will be kept during your current session.  

> _Tips 1:_ pls cleanup tomcat working cache before firstly using it.
> _Tips 2:_ don't forget enable `show comments` at tab `HTML` when you use Firebug to view the page source.

## FAQ
**Q:** How to resolve if name conflict occurred with the default debugging switch key name `pageDebugging`?  
**A:** You can specify a new debugging switch key name by appending JVM argument:  
`-DpageDebugging.switchKey=[new debugging switch key name]`

**Q:** If I want to specify some files or dirs not to be tracked, can I? How?  
**A:** Yes, you can. By default all files under `/WEB-INF/tags/` (include its sub dirs). If your requirement is different, you can change it by appending one more JVM argument like below:  
`-DpageDebugging.excluded=”foo/bar/, tttt.txt”`  
Or  
`-DpageDebugging.excluded_append:“foo/bar/, tttt.txt”`

> _Notes:_
>> 1) Please use ‘,’ or ‘;’ to split these files or dirs.  
>> 2) ‘append:’ means append to current item list.  
>> 3) Don’t worry about the redundant whitespaces between them.  

**Q:** As we know, the switch status info was kept into the user current session. But if we need to change it only in a request life scope, can we? How?  
**A:** Yes, we can. Append `req_pageDebugging=on/off` to the url, then you can change the configuration temporarily, and the configuration in the session will be kept unchanged.

@author: Minglei Tu (tmlneu@gmail.com)  
Licensing conditions (BSD-style) can be found in LICENSE.txt.  

- - - - - - - - - - -  
- - - - - - - - - - -  
Page-Debugging 用户指南
=====================
## 概述
现在的系统 Web 页面多半由层层嵌套的页面片段组合合成。这些页面片段在提高代码重用性的同时，也增加了文件查找的困难。这个小工具能让页面文件的查找不那么痛苦。

## 功能
Page-Debugging 的功能可简述如下：  

####在使用这个工具之前，页面 html 代码是这样的，  
  ![before using page-debugging](https://github.com/tminglei/page-debugging/raw/master/doc/before-using-page-debugging.png)  

####但是在使用了这个工具 _(在当前 url 后面添加参数`pageDebugging=on`)_ 之后，  
 ![after using page-debugging](https://github.com/tminglei/page-debugging/raw/master/doc/after-using-page-debugging.png)

你看，现在的 html 代码多了些`<!—[debugging code] - start -->`、`<!-- [debugging code] - end -->`之类的注释。这样一来，你所关心的某块页面内容产生于哪个页面文件就一目了然了。

> 注：debugging code 的格式是：`‘L’ + [层号] + ‘#’ + [序列号] + ‘:’ + [文件名]`，所以你可以从中获得以下信息：  
> > a)	所包含文件的路径  
> > b)	文件输出内容的起止  
> > c)	这个文件被嵌在当前页面的哪一层  

## 技术原理
使用 AspectJ 技术，在web容器或者模板引起包含（include）一个页面文件的时候，在该文件输出内容的头尾插入了用于追踪的debugging code。

> 支持 Tomcat 6.0 以上，JBoss-Web 2, Freemarker 2 以上以及 Velocity 1.5/1.7

## 安装和配置

####1. 让 AspectJ 有机会拦截虚拟机的类加载
> a) 确保你机器上有 [aspectjweaver.jar](https://github.com/tminglei/page-debugging/raw/master/dist/aspectjweaver.jar)  
> b) 添加 JVM 参数： `-javaagent:[pathto]\aspectjweaver.jar`  

如果你是用 Eclipse 加上那个 Tomcat 插件来开发的，那你可以像这样来做：  
  ![configure jvm arguments in eclipse](https://github.com/tminglei/page-debugging/raw/master/doc/configure-jvm-arguments-in-eclipse.png)  

####2. 配置 Tomcat 和你的应用
> a) 把 [page-debugging.jar](https://github.com/tminglei/page-debugging/raw/master/dist/page-debugging-1.5.3.jar) 和 [aspectjrt.jar](https://github.com/tminglei/page-debugging/raw/master/dist/aspectjrt.jar) 放到 “[tomcat安装目录]\lib” 下；   
> b) 给你的应用配置一个定制的 class loader，这样你应用间接用的 page debugging tool 相关的代码可以被你应用的 class loader 加载，从`page-debugging.jar` (别忘了，`page-debugging.jar`已经被部署到了: `[tomcat_home]\lib`);  
> c) 在 Tomcat Server.xml 中配上一个用作开关的监听器（Valve），  

 ![configure tomcat server.xml (lite)](https://github.com/tminglei/page-debugging/raw/master/doc/configure-tomcat-server-xml.png)

> valve: `tml.pagedebugging.PageDebuggingValve`  
> class loader: `tml.pagedebugging.classloader.BridgedClassLoader`  

####3. 现在，我们就可以通过在当前 url 后面添加 `pageDebugging=true/on` 或者 `pageDebugging=false/off`参数来打开/关闭 page debugging 功能了。

> 注：上述配置信息是保存在用户当前 session 里的。  

> _提示1_：初次使用之前，最好清一下 Tomcat 缓存，这样所有的 jsp 都会重新编译。
> _提示2_：如果你是通过 Firebug 来看页面源码，记得打开 HTML 标签下的 '显示注释' 开关。


## FAQ
**Q:** 如果`pageDebugging`参数名不巧已被占用了，怎么办？  
**A：**你可以通过JVM参数指定另一个名字，具体这样做：  
`-DpageDebugging.switchKey=[new debugging switch key name]`

**Q：**我可以指定某些文件或目录不被 track 吗？  
**A：**可以的。默认情况下，`/WEB-INF/tags/`目录（包括子目录）下的文件都不会被 track。你可以通过以下方式改写或追加：  
`-DpageDebugging.excluded=”foo/bar/, tttt.txt”`  
或者  
`-DpageDebugging.excluded_append:“foo/bar/, tttt.txt”`  

> 说明：
> > 1)	请用英文逗号（,）或分号（;）来分隔；  
> > 2)	‘append:’表示追加  
> > 3)	不用担心多余的空格，page debugging 会抹掉它们的  

**Q：**如前所述，开关状态信息是保存在 session 里面的。但是如果我只想在某一个 request 里面临时的改变一下开关，能办到吗？  
**A：**是的，可以办到。把`req_pageDebugging=on/off`添加到url，你就可以临时的改变一下开关状态，而保存在 session 里的开关信息不受影响。

作者: 涂名雷 (tmlneu@gmail.com)  
Licensing conditions (BSD-style) can be found in LICENSE.txt.  
