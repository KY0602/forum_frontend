# 代码模块介绍
*注意标黑的部分*
## 主模块
- LoginActivity.java, RegisterActivity.java, activity_login.xml, activity_register.xml :
  - 注册、登录相关
- MainActivity.java，activity_main.xml : 
  - 初始化各个fragment
- BottomBarAdapter.java，SmartFragmentStatePagerAdapter.java : 
  - 底部导航（Bottom Navigation）和fragment的adapter，应该不需要修改

## 动态列表页面（Home)
- HomeFragment.java，fragment_home.xml : 
  - 动态页面的主要部分
  - 登录后会将user_id传过来，其他界面要使用的话要从这里拿（具体可参考ProfileFragment onCreateView那里的方法获取user_id)
- PagerAdapter.java : 
  - Tab的adapter，初始化时创建动态（连后端的话，可能要在这里获取动态列表），通过setArguments将动态传给Tab
  - **从后端提取动态列表的方法可以参考PersonalPageActivity.java中getStatusList，URL要改成query-all-status或query-followed-status**
- TabFragment.java，fragment_tab.xml : 
  - Tab组件，“所有用户动态”和“已关注用户动态”都属于这个类，通过getArguments获取动态列表
- WordListAdapter.java，wordlist_item.xml : 
  - RecyclerView的adapter，在这里处理RecyclerView的设计（？），onBindViewHolder处理点击动态后的跳转

### 动态页面（Status）
- **Status.java**
  - **动态类，用于后端传入时**
- StatusActivity.java，activity_status.xml，activity_status_music.xml，activity_status_video.xml : 
  - 动态详情页面的主要部分，通过intent获取动态详情，目前是会根据传入的type判断是属于纯文字、图片、音频或视频，继而调用不同的xml
  - 目前没有区分纯文字和图片
  - **因为这部分在“个人主页”部分有用到，所以我写了从后端提取动态详情（图片）的简单实现。需要传入status_id和个人user_id，目前的版本如果从“动态页面”跳转的话这两个参数是写死为0和0的（在WordListAdapter.java那里）因此可能会崩溃**
- ImageService.java : 
  - 需传入参数image_type(profile/status)和image_name
  - 会将网络图片保存到Pictures/download_tmp里，下载完成后会向StatusActivity发送Broadcast，接收到后再读取图片放到界面中
- MusicService.java :
  - 音乐播放器的service，目前是从本地（/Music)读取音频文件（写死为nevergonna.mp3)，然后通过MediaPlayer播放
- VideoService.java :
  - 视频播放器的service，同样从本地（/DCIM/Movies)读取视频文件（写死为nevergonna.mp4)，然后通过MediaPlayer播放

## 发布页面（Post)
- PostFragment.java，fragment_post.xml ：
  - 发布页面的主要部分，目前只能发布纯文字动态。
  - 通过SharedPreferences自动保存未发布动态（草稿），下次打开页面时自动读入草稿。草稿目前只能保存一个。
  - 点击发布后，调用switchContent来调用MainActivity中的switchHome，在switchHome里再把新动态添加到列表中。

## 搜索页面（Search)
- SearchFragement.java, fragment_search.xml : 目前还没有实际功能

## 个人页面（Profile)
- ProfileFragment.java, fragment_profile.xml: 
  - 从后端提取个人信息（用户名、邮箱、简介、头像）
  - 头像下载与展示方法与动态图片一样，若文件不存在则启动ImageService下载到本地（download_tmp)，否则直接读取先前保存的即可
  - 点击Button，通过startActivityForResult跳转到EditProfileActivity，onActivityResult接收传后来的信息进行替换（用户名、简介、头像），头像替换方法也是通过ImageService
- EditProfileActivity.java，activity_edit_profile.xml:
  - 修改和保存用户名、简介和头像
  - 点击头像启动Gallery Activity从Gallery选择图片，选择完毕后在onActivityResult接收，并转换为路径名（保存为img_src)，通过uploadImage上传之后端
  - 点击Button将修改后的个人信息保存到后端，并退回Profile Fragment（会将修改后的用户名、简介和头像名传回去）
- ChangePasswordActivity.java, activity_change_password.xml:
  - 修改密码
  - 同样通过startActivityForResult从ProfileFragment跳转
- FollowingListActivity.java, activity_following_list.xml:
  - 展示“已关注用户”列表
  - 需传入两个参数（user_id_self和user_id_other)，若从Profile Fragment（自己的个人信息页面）跳转的话两者都是自己的user_id
- OtherUserProfileActivity.java, activity_other_user_profile.xml:
  - 他人的个人信息页面
  - 可对用户进行“关注/取关”和“屏蔽/解除屏蔽”操作
  - 需传入两个参数（user_id_self和user_id_other），以执行上面的操作
  - **动态部分，在点击其他人用户名跳转到这里，具体跳转方式就是开启Intent并传入两个参数即可**
- PersonalPageActivity.java, activity_personal_page.xml
  - 个人主页
  - 需传入两个参数（user_id_self和user_id_other）
  - 可以通过点击动态进入“动态详情页面”（StatusActivity）
- StatusItemAdapter，status_list_item_layout.xml
  - ListView中item的Adapter和layout
