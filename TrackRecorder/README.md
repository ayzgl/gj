# 轨迹记录器 - TrackRecorder

一个简洁优雅的Android应用，用于记录和查看您的运动轨迹。

## 功能特性

- 📍 **实时轨迹记录** - 使用GPS精确记录您的运动轨迹
- 🗺️ **地图展示** - 在Google Maps上查看轨迹路线
- 📊 **数据统计** - 显示距离、时长、速度等详细信息
- 📱 **简洁界面** - 采用Material Design 3设计语言
- 💾 **本地存储** - 使用Room数据库安全保存数据
- 📤 **导出功能** - 支持导出GPX格式（开发中）
- 🔒 **隐私保护** - 所有数据仅存储在您的设备上

## 技术栈

- **语言**: Kotlin
- **架构**: MVVM + Repository模式
- **UI框架**: AndroidX + Material Design 3
- **数据库**: Room (SQLite)
- **地图服务**: Google Maps SDK
- **位置服务**: Google Play Services Location
- **异步处理**: Kotlin Coroutines

## 项目结构

```
app/src/main/
├── java/com/trackrecorder/app/
│   ├── activities/          # Activity界面
│   │   ├── MainActivity     # 主界面
│   │   ├── HistoryActivity  # 历史记录
│   │   └── MapActivity      # 地图展示
│   ├── adapters/            # RecyclerView适配器
│   │   └── TrackAdapter     # 轨迹列表适配器
│   ├── dao/                 # Room数据库访问对象
│   ├── models/              # 数据模型
│   ├── repository/          # 数据仓库
│   ├── services/            # 后台服务
│   │   └── TrackingService  # 轨迹记录服务
│   ├── utils/               # 工具类
│   └── viewmodel/           # ViewModel
└── res/                     # 资源文件
    ├── layout/              # 布局文件
    ├── values/              # 字符串、颜色、主题
    └── xml/                 # 配置文件
```

## 配置要求

### 1. Google Maps API Key

要使用地图功能，您需要配置Google Maps API Key：

1. 访问 [Google Cloud Console](https://console.cloud.google.com/)
2. 创建新项目或选择现有项目
3. 启用 **Maps SDK for Android**
4. 创建API密钥
5. 在CodeMagic中配置环境变量 `MAPS_API_KEY`

### 2. 代码签名（用于发布）

在CodeMagic中配置发布签名：

1. 生成密钥库文件：
   ```bash
   keytool -genkey -v -keystore release.keystore -alias trackrecorder -keyalg RSA -keysize 2048 -validity 10000
   ```

2. 在CodeMagic中上传密钥库：
   - 进入 **Team settings** > **Code signing identities** > **Android keystores**
   - 上传 `release.keystore` 文件
   - 设置引用名称为 `trackrecorder_keystore`

3. 配置环境变量：
   - `CM_KEYSTORE_PASSWORD`
   - `CM_KEY_ALIAS` (trackrecorder)
   - `CM_KEY_PASSWORD`

### 3. Google Play发布（可选）

如果需要发布到Google Play：

1. 在Google Play Console创建应用
2. 设置Google Cloud服务账号
3. 在CodeMagic中配置凭证组 `google_play_credentials`
4. 设置环境变量 `GCLOUD_SERVICE_ACCOUNT_CREDENTIALS`

## 构建和发布

### 使用CodeMagic构建

本项目已配置好CodeMagic CI/CD，支持自动构建和发布。

#### 工作流

1. **android-release**: 构建发布版本 (AAB + APK)
   - 自动签名
   - 版本号自动递增
   - 可发布到Google Play

2. **android-debug**: 构建调试版本 (APK)
   - 快速构建用于测试
   - 无需签名配置

#### 手动触发构建

1. 访问 [CodeMagic](https://codemagic.io/start/)
2. 连接您的Git仓库
3. 选择 `TrackRecorder` 项目
4. 配置必要的环境变量
5. 点击 **Start new build**

#### 本地构建

```bash
# 克隆项目
git clone <repository-url>
cd TrackRecorder

# 安装依赖
flutter pub get

# 构建Debug版本
flutter build apk --debug

# 构建Release版本
flutter build apk --release
flutter build appbundle --release
```

## 权限说明

应用需要以下权限：

- **ACCESS_FINE_LOCATION**: 精确位置信息（必需）
- **ACCESS_COARSE_LOCATION**: 粗略位置信息（必需）
- **ACCESS_BACKGROUND_LOCATION**: 后台位置访问（Android 10+）
- **INTERNET**: 网络访问（用于地图）
- **WRITE_EXTERNAL_STORAGE**: 存储轨迹数据（Android 9及以下）
- **FOREGROUND_SERVICE**: 前台服务（保持轨迹记录）

## 配置CodeMagic环境变量

在CodeMagic的项目设置中配置以下环境变量：

### 必需

- `MAPS_API_KEY`: Google Maps API密钥

### 发布时必需

- `CM_KEYSTORE_PATH`: 密钥库路径（由CodeMagic管理）
- `CM_KEYSTORE_PASSWORD`: 密钥库密码
- `CM_KEY_ALIAS`: 密钥别名
- `CM_KEY_PASSWORD`: 密钥密码

### 可选（Google Play发布）

- `GCLOUD_SERVICE_ACCOUNT_CREDENTIALS`: Google Cloud服务账号凭证JSON
- `GOOGLE_PLAY_TRACK`: 发布轨道 (internal/alpha/beta/production)

## 首次运行设置

1. 构建并安装应用
2. 授予位置权限
3. 确保GPS已开启
4. 点击"开始记录"按钮
5. 应用将在后台记录轨迹
6. 点击"停止记录"保存轨迹
7. 在"历史记录"中查看和管理轨迹

## 常见问题

### Q: 应用无法获取位置？
A: 请检查：
- 位置权限已授予
- GPS已开启
- 设备不在飞行模式

### Q: 地图无法显示？
A: 请检查：
- Google Maps API Key已正确配置
- 设备有网络连接

### Q: 轨迹记录中断？
A: 某些设备会限制后台应用，请将应用添加到电池优化白名单。

## 开发计划

- [ ] GPX文件导出功能
- [ ] 轨迹分享功能
- [ ] 轨迹统计分析
- [ ] 离线地图支持
- [ ] 语音播报功能
- [ ] 多运动模式（跑步、骑行、驾车等）
- [ ] 心率监测集成
- [ ] 云端同步

## 贡献

欢迎提交Issue和Pull Request！

## 许可证

MIT License

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交GitHub Issue
- 发送邮件到项目维护者

## 版本历史

### v1.0.0 (2024-03-28)
- 初始版本发布
- 核心轨迹记录功能
- 地图展示功能
- 历史记录管理