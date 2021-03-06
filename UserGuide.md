### 文件

#### 用户文件

**路径**

每个用户拥有一个自己的文件夹，彼此独立。该文件夹的绝对路径为：**data-root/用户名**，其中data-root由管理员设定，默认是/raid/gcmp/data。所以以用户user0为例，其上传的所有文件都是在/raid/gcmp/data/user0目录下，这个目录也是user0能访问的最顶层目录（也就是下图中的 **...**）。

![用户文件小框.png](https://i.loli.net/2020/11/17/7ga4ZrcGU2IkxlM.png)

**操作**

上传前先选择要上传的服务器，服务器的具体信息见**可用资源**页面。

目前仅支持文件上传，因为数据集往往比较大，建议打包成压缩文件上传然后解压。

上传过程中如果出现网络波动，刷新网页后重新上传即可，已上传的部分不会丢失。

每个用户就一个根目录，不区分代码和数据集，如有需要请自行创建子文件夹。



#### 公共数据集

在公共数据集页面可以看到所有可用的公共数据集。



### 镜像

#### 用户镜像

需要自己写dockerFile，目前可以上传，但是任务创建时还不支持使用用户镜像。

#### 公共镜像

在公共镜像页面可以看到所有的可用公共镜像。



### 任务

#### 路径

每个用户的工作路径就是用户文件根目录（/raid/gcmp/data/user0），所以如果代码中用到的是自己的数据集，可以写相对路径（./xx/xxx），也可以写绝对路径（/raid/gcmp/data/user0/xx/xxx），建议用相对路径，方便在自己电脑上调试，只要保持代码和数据的相对位置在本机和服务器上一致就可以了。如果用到的是公共数据集，就写公共数据集页面看到的绝对路径（/raid/gcmp/commonDataset/modelnet40_ply_hdf5_2048）。

**以/开头的就会被判断成绝对路径，相对路径可以由./开头，或者省略./！！**

每个任务都是由k8s执行，其底层由docker容器执行，**容器内只能看到用户文件根目录和公共数据集目录**，所以**如果在训练任务中要产生文件，只能保存在用户根目录以下的文件夹中（/raid/gcmp/data/user0/之后随便）！！！**

#### 操作

**上传**

![user_task小框.png](https://i.loli.net/2020/11/17/nreRmUVvZA3yMCF.png)

先选服务器，再选GPU数，选择GPU数按钮下拉后能看到的就是所有可选的数字（1-剩余GPU数），这个数字是实时更新的，但如果刚好有两个人一起选，有一个人提交任务时就会报错了，这时重新选GPU数就好了，或者换服务器。

命令（cmd）执行的根目录就是用户文件根目录，以python 为例，如果有一个train.py文件绝对路径为/raid/gcmp/data/user0/train.py，那命令就输入python train.py。如果其绝对路径为/raid/gcmp/data/user0/xx/train.py，那命令就输入python ./xx/train.py。

**如果用到的镜像同时支持python2和python3，那在命令里面就用python和python3区分。否则都用python。**



**查看训练过程**

![user_task_log.png](https://i.loli.net/2020/11/17/xodBmrCFk6b2jEN.png)




- 运行状态

总共有Running，Pending，Succeeded，Failed四种状态。

Running代表正在运行。

Pending代表正在创建任务，如果卡在这里比较久可能是你需要的镜像服务器上没有，需要去网上下，就会卡一段时间。

Succeeded代表运行完成。

Failed代表运行出错。

在四种情况下都可以删除任务。Running和Pending的任务会占用GPU，如果删除了就会释放GPU，Succeeded和Failed的任务即使不手动删除也会自动释放GPU。



- 标准输出流的输出

点击查看日志就能看到标准输出流的输出了。其每次显示一定数量的最新的输出，不会显示所有的输出。所以该功能用于检查代码是否正确运行，准确率之类的指标还是应该用文件或者数据库存储。

日志永久存储，即使任务状态为Succeeded或者Failed，如果不需要了请及时删除任务，这样日志也会被删除。



