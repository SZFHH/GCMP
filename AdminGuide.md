### 初始化

先设置项目配置文件中的gcmp.admin-name，然后登录xxx:8090/admin进行初始化，初始化后该网址就是管理员登录页面。

### 用户管理

账号是分配制的，不支持个人注册。由管理员设置用户名，密码（同用户名），私人镜像数量。

[![DEIkSs.jpg](https://s3.ax1x.com/2020/11/17/DEIkSs.jpg)](https://imgchr.com/i/DEIkSs)

注销账户会导致跟该用户相关的所有信息都被删除，包括用户文件，用户镜像，用户任务。



### 任务管理

管理员在后台能看到所有任务，并且能够取消任务。



### 文件管理

项目中涉及到的文件路径有：

- gcmp-root：所有文件的根路径，也就是管理员所能访问的最上层路径，默认是 /raid/gcmp
- common-data-root：公共数据集根路径，默认是 /raid/gcmp/commonDataset
- data-root：用户文件根路径，默认是/raid/gcmp/data
- temp-file-root：临时文件路径，因为上传是分片上传，所以会有临时文件，默认是 /raid/gcmp/temp

所以在默认配置下管理员看到的文件页面如下：

[![DEoSj1.png](https://s3.ax1x.com/2020/11/17/DEoSj1.png)](https://imgchr.com/i/DEoSj1)

以上描述的是每台工作节点（带GPU服务器）上的文件目录结构。

master上的文件结构如下：

- docker-file-root：用于存储每个用户的dockerfile，每个用户有一个文件夹，如docker-file-root/user0
- task-log-root：永久存储任务的输出，每个用户有一个文件夹，在里面以任务ID为文件名，存储日志，如用户user0的id为5的任务日志就存储在：docker-file-root/user0/5。

### 公共镜像管理

镜像名和详细信息可以随便输，**docker hub tag**必须要在docker hub中能找到！



### 公共数据集管理

目前上传公共数据集的方式为在文件管理中，把数据上传到common-data-root路径下，然后在公共数据集管理页面点击同步按钮，common-data-root下的文件夹信息就会同步到数据库，用户就可以看到公共数据集信息了。