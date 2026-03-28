# CodeMagic 快速开始指南

## 上传项目到CodeMagic

### 步骤 1: 访问 CodeMagic

打开浏览器，访问 https://codemagic.io/start/

### 步骤 2: 登录/注册

- 使用GitHub/GitLab/Bitbucket账号登录
- 或者使用邮箱注册新账号

### 步骤 3: 添加应用

1. 点击 **Add application**
2. 选择您的Git仓库提供商
3. 选择 **TrackRecorder** 仓库
4. 选择 **Android** 作为项目类型
5. 点击 **Add application**

### 步骤 4: 配置构建

#### 必需配置

在CodeMagic的项目设置中，配置以下环境变量：

**Google Maps API Key:**
- 变量名: `MAPS_API_KEY`
- 值: 您的Google Maps API密钥

**获取API Key的方法:**
1. 访问 [Google Cloud Console](https://console.cloud.google.com/)
2. 创建新项目
3. 启用 **Maps SDK for Android**
4. 创建API密钥（限制为Android应用）
5. 将应用包名 `com.trackrecorder.app` 和SHA-1指纹添加到限制中

#### 发布配置（可选）

如果要构建发布版本，需要配置代码签名：

**步骤 A: 生成密钥库**

如果您还没有密钥库，先生成一个：

```bash
keytool -genkey -v -keystore release.keystore -alias trackrecorder -keyalg RSA -keysize 2048 -validity 10000
```

**步骤 B: 在CodeMagic中配置签名**

1. 进入 **Team settings** > **Code signing identities** > **Android keystores**
2. 点击 **Add keystore**
3. 上传您的 `release.keystore` 文件
4. 设置 **Reference name**: `trackrecorder_keystore`
5. 填写密钥库密码、别名和密码
6. 点击 **Add keystore**

**步骤 C: 配置环境变量**

在应用级别的环境变量中配置：

```bash
CM_KEYSTORE_PATH=/tmp/keystore.keystore  # CodeMagic会自动处理
CM_KEYSTORE_PASSWORD=您的密钥库密码
CM_KEY_ALIAS=trackrecorder
CM_KEY_PASSWORD=您的密钥密码
```

### 步骤 5: 触发构建

#### 方法 1: 手动构建

1. 在CodeMagic控制台中，选择 **TrackRecorder** 应用
2. 点击 **Start new build**
3. 选择要构建的分支（如 `main`）
4. 选择工作流：
   - **android-release**: 构建发布版本（需要签名配置）
   - **android-debug**: 构建调试版本（无需签名配置）
5. 点击 **Start new build**

#### 方法 2: 自动构建（推荐）

在 `codemagic.yaml` 中配置自动触发：

```yaml
# 在 workflows 下添加触发器
triggering:
  events:
    - push
  branch_patterns:
    - pattern: main
      include: true
      source: true
```

### 步骤 6: 下载构建产物

构建完成后：

1. 进入构建详情页面
2. 在 **Artifacts** 标签页中找到构建产物
3. 下载：
   - **.aab** 文件: 用于Google Play发布
   - **.apk** 文件: 用于手动安装测试

### 步骤 7: 安装测试

#### 安装APK

```bash
# 连接Android设备，然后执行
adb install app-release.apk
```

#### 或者传输到设备安装

1. 将APK文件传输到Android设备
2. 在设备上点击APK文件
3. 允许安装来自未知来源的应用
4. 完成安装

## 故障排除

### 问题 1: 构建失败 - "Keystore not found"

**解决方案**: 确保在CodeMagic中正确配置了密钥库和环境变量。

### 问题 2: 地图显示为空白

**解决方案**: 
- 检查 `MAPS_API_KEY` 是否正确配置
- 确保API密钥已启用 Maps SDK for Android
- 检查API密钥是否限制了应用包名和SHA-1指纹

### 问题 3: 无法获取位置

**解决方案**:
- 确保设备已开启GPS
- 授予应用位置权限
- 在室外测试以获得更好的GPS信号

### 问题 4: 构建超时

**解决方案**:
- 在 `codemagic.yaml` 中增加 `max_build_duration`
- 或者使用更快的构建实例类型

## 高级配置

### Google Play自动发布

要启用自动发布到Google Play：

1. 在Google Play Console创建应用
2. 设置Google Cloud服务账号
3. 在CodeMagic中添加凭证组：
   - 进入 **Team settings** > **Codemagic API keys** > **Environment variable groups**
   - 创建新组 `google_play_credentials`
   - 添加变量 `GCLOUD_SERVICE_ACCOUNT_CREDENTIALS` (服务账号JSON内容)

4. 在 `codemagic.yaml` 中配置发布：

```yaml
publishing:
  google_play:
    credentials: $GCLOUD_SERVICE_ACCOUNT_CREDENTIALS
    track: internal  # 或 alpha, beta, production
```

### Slack通知

配置Slack通知：

1. 在Slack中创建Incoming Webhook
2. 在CodeMagic中配置环境变量 `SLACK_WEBHOOK_URL`
3. 在 `codemagic.yaml` 中添加：

```yaml
publishing:
  slack:
    channel: '#builds'
    notify_on_build_start: true
```

## 支持

如有问题：

1. 查看 [CodeMagic文档](https://docs.codemagic.io/)
2. 检查 [Google Maps SDK文档](https://developers.google.com/maps/documentation/android-sdk/)
3. 提交GitHub Issue

## 下一步

- [ ] 测试调试版本
- [ ] 配置发布签名
- [ ] 添加Google Maps API Key
- [ ] 构建发布版本
- [ ] 提交到Google Play (可选)