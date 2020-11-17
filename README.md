<h1 align="center"><a href="https://github.com/SZFHH/GCMP" target="_blank">GCMP</a></h1>

### 简介

GCMP(GPU Cluster Management Platform） GPU集群管理平台

代码基于Spring Boot，底层用k8s进行GPU分配和执行训练任务。

实现对多台GPU服务器文件、镜像、GPU调度的统一管理。



### 快速开始

GPU集群由一台master节点和多台从节点组成，最好以一台不带GPU的服务器作为master节点，如果没有的话把其中一台GPU服务器作为master节点也可以。

#### 安装docker和nvidia-docker2

每台服务器上都要，配置参考请移步[docker及nvidia-docker2安装步骤](https://github.com/SZFHH/GCMP/blob/master/config/docker.txt)

如果主节点是不带GPU服务器的话，主节点上只需要docker，不需要nvidia-docker2。

请移步[docker远程配置](https://github.com/SZFHH/GCMP/blob/master/config/docker%E8%BF%9C%E7%A8%8B.txt)，开启java远程对docker的访问。

#### 配置k8s

整体有点复杂，如果遇到问题google一下或者留言都可以。

##### 配置master节点

请移步[master配置](https://github.com/SZFHH/GCMP/blob/master/config/master_k8s.txt)

如果master节点是用的GPU服务器，并且希望master节点上的GPU也参与调度，执行以下命令

```shell
kubectl taint node k8s-master node-role.kubernetes.io/master-
```

##### 配置从节点

请移步[slave配置](https://github.com/SZFHH/GCMP/blob/master/config/slave_k8s.txt)

#### 启动主程序

java程序运行在主节点上，以下几个是关键配置：

- gcmp.server-properties：在里面写上所有的带GPU的服务器。

- gcmp.ftp-type：可以是ftp或者sftp，建议用ftp（比较快，并且经过比较完善的测试）。

- gcmp.admin-name：可以设置管理员的用户名，默认是admin。

- 在gcmpConst中设置硬盘挂载位置DISK_MOUNT_PATH

第一次启动时先进行初始化，访问 http://127.0.0.1:8090/admin  ，其会自动跳转到初始化页面。

用户访问 http://127.0.0.1:8090

### 预览图

用户 - 登录
![user_login.jpg](https://i.loli.net/2020/11/17/lAU7jqo5LPRbIty.jpg)

用户 - 我的任务
![user_task.jpg](https://i.loli.net/2020/11/17/L3y4pRIr9jmW1fM.jpg)

用户 - 我的文件
![user_data.jpg](https://i.loli.net/2020/11/17/djFAbTXmfkPiKZx.jpg)

用户 - 我的镜像
![user_image.jpg](https://i.loli.net/2020/11/17/wnx3eAOWaLf94Xo.jpg)

用户 - 公共镜像
![user_common_image.jpg](https://i.loli.net/2020/11/17/IV3GHu68cmRvirY.jpg)

用户 - 公共数据集
![user_common_data.jpg](https://i.loli.net/2020/11/17/ejxH4d98rwpFEK3.jpg)

管理员 - 用户管理
![admin_user_info.jpg](https://i.loli.net/2020/11/17/2TRWUIvs3JZuXKY.jpg)

管理员 - 任务管理
![admin_task.jpg](https://i.loli.net/2020/11/17/qN3wbkHmoQa7pGC.jpg)

管理员 - 文件管理
![admin_data.jpg](https://i.loli.net/2020/11/17/HDJVoX7uOftaecm.jpg)

管理员 - 公共镜像管理
![admin_common_image.jpg](https://i.loli.net/2020/11/17/1yL6tXilnYUaFzc.jpg)

管理员 - 公共数据集管理
![admin_common_data.jpg](https://i.loli.net/2020/11/17/bQ8mjhHd1RlrXEI.jpg)

服务器空闲资源资源
![server_status.jpg](https://i.loli.net/2020/11/17/mu1jtc7i3oMhwO6.jpg)


### 使用指南

用户使用指南，请移步[user guide](https://github.com/SZFHH/GCMP/blob/master/UserGuide.md)

管理员使用指南，请移步 [admin guide](https://github.com/SZFHH/GCMP/blob/master/AdminGuide.md)




