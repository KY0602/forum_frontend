# 代码模块介绍
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
- PagerAdapter.java : 
  - Tab的adapter，初始化时创建动态（连后端的话，可能要在这里获取动态列表），通过setArguments将动态传给Tab
- TabFragment.java，fragment_tab.xml : 
  - Tab组件，“所有用户动态”和“已关注用户动态”都属于这个类，通过getArguments获取动态列表
- WordListAdapter.java，wordlist_item.xml : 
  - RecyclerView的adapter，在这里处理RecyclerView的设计（？），onBindViewHolder处理点击动态后的跳转

### 动态页面（Status）
- StatusActivity.java，activity_status.xml，activity_status_music.xml，activity_status_video.xml : 
  - 动态详情页面的主要部分，通过intent获取动态详情，目前是会根据传入的type判断是属于纯文字、图片、音频或视频，继而调用不同的xml
- ImageService.java : 
  - 下载网络图片的service，会将网络图片保存到Pictures/download_tmp里，网址和文件名目前都是写死的，下载完成后会向StatusActivity发送Broadcast，接收到后再读取图片放到界面中
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
- ProfileFragment.java, fragment_profile.xml : 目前还没有实际功能
