package com.love.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 用户表，用来保存用户基本信息 实体类
 * 
 * @author generator
 * @since 1.0
 */
@TableName(value = "etc_user")
public class EtcUser extends Model<EtcUser> {

    private static final long serialVersionUID = 1L;

    // columns START

    @TableId(value = "id", type = IdType.ID_WORKER)
    private long id; // id

    @TableField(value = "user_code")
    private String userCode; // 用户编码

    @TableField(value = "user_name")
    private String userName; // 用户名

    @TableField(value = "real_name")
    private String realName; // 真实姓名

    private java.math.BigDecimal balance; // 余额

    private Integer points; // 积分

    private String password; // 密码

    private Integer status; // 状态

    private String phone; // 电话

    @TableField(value = "device_id")
    private String deviceId; // 设备ID

    private String email; // 邮箱

    @TableField(value = "id_card")
    private String idCard; // 身份证

    private Integer sex; // 性别1男；2女

    private Integer age; // 年龄

    @TableField(value = "identity_auth")
    private Integer identityAuth; // 是否有身份认证

    @TableField(value = "phone_auth")
    private Integer phoneAuth; // 是否有电话认证

    @TableField(value = "vehicle_auth")
    private Integer vehicleAuth; // 是否车辆认证

    @TableField(value = "payment_auth")
    private Integer paymentAuth; // 是否添加账户

    @TableField(value = "email_auth")
    private Integer emailAuth; // 是否有邮箱认证

    @TableField(value = "district_code")
    private String districtCode; // 区域

    private String avatar; // 头像

    @TableField(value = "drive_license_id")
    private String driveLicenseId; // 驾驶证号

    @TableField(value = "idcard_frontside_pic")
    private String idcardFrontsidePic; // 身份证正面照片

    @TableField(value = "idcard_backside_pic")
    private String idcardBacksidePic; // 身份证反面照片

    @TableField(value = "drive_license_pic")
    private String driveLicensePic; // 驾驶证照片

    private Integer flags; // 标识

    @TableField(value = "application_id")
    private String applicationId; // 申请编号

    @TableField(value = "utm_source")
    private String utmSource; // 媒介来源

    @TableField(value = "utm_medium")
    private String utmMedium; // 跟踪媒介

    @TableField(value = "client_version")
    private String clientVersion; // 系统版本

    @TableField(value = "register_id")
    private String registerId; // 设备注册ID

    @TableField(value = "auto_pay")
    private int autoPay; // 自动付（0:关闭；1:启用）

    @TableField(value = "send_type")
    private int sendType; // 消息推送类型
    @TableField(value = "small_routine_login")
    private Integer smallRoutineLogin; // 小程序登陆标识（0：未登陆；1：已登陆）
    @TableField(value = "wechat_login")
    private int wechatLogin; // 微信登陆标识（0：未登陆；1：已登陆）
    @TableField(value = "wechat_nickname")
    private String wechatNickname; // 消息推送类型

    private String remark; // 备注

    @TableField(value = "create_time")
    private Date createTime; // 创建时间

    @TableField(value = "update_time")
    private Date updateTime; // 更新时间
    // columns END

    @TableField(exist = false)
    private Integer contractState;// 是否签约


    public Integer getContractState() {
        return contractState;
    }

    public void setContractState(Integer contractState) {
        this.contractState = contractState;
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public java.math.BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(java.math.BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public int getAutoPay() {
        return autoPay;
    }

    public void setAutoPay(int autoPay) {
        this.autoPay = autoPay;
    }

    public Integer getIdentityAuth() {
        return identityAuth;
    }

    public void setIdentityAuth(Integer identityAuth) {
        this.identityAuth = identityAuth;
    }

    public Integer getPhoneAuth() {
        return phoneAuth;
    }

    public void setPhoneAuth(Integer phoneAuth) {
        this.phoneAuth = phoneAuth;
    }

    public Integer getVehicleAuth() {
        return vehicleAuth;
    }

    public void setVehicleAuth(Integer vehicleAuth) {
        this.vehicleAuth = vehicleAuth;
    }

    public Integer getPaymentAuth() {
        return paymentAuth;
    }

    public void setPaymentAuth(Integer paymentAuth) {
        this.paymentAuth = paymentAuth;
    }

    public Integer getEmailAuth() {
        return emailAuth;
    }

    public void setEmailAuth(Integer emailAuth) {
        this.emailAuth = emailAuth;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDriveLicenseId() {
        return driveLicenseId;
    }

    public void setDriveLicenseId(String driveLicenseId) {
        this.driveLicenseId = driveLicenseId;
    }

    public String getIdcardFrontsidePic() {
        return idcardFrontsidePic;
    }

    public void setIdcardFrontsidePic(String idcardFrontsidePic) {
        this.idcardFrontsidePic = idcardFrontsidePic;
    }

    public String getIdcardBacksidePic() {
        return idcardBacksidePic;
    }

    public void setIdcardBacksidePic(String idcardBacksidePic) {
        this.idcardBacksidePic = idcardBacksidePic;
    }

    public String getDriveLicensePic() {
        return driveLicensePic;
    }

    public void setDriveLicensePic(String driveLicensePic) {
        this.driveLicensePic = driveLicensePic;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUtmSource() {
        return utmSource;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getSmallRoutineLogin() {
        return smallRoutineLogin;
    }

    public void setSmallRoutineLogin(Integer smallRoutineLogin) {
        this.smallRoutineLogin = smallRoutineLogin;
    }

    public int getWechatLogin() {
        return wechatLogin;
    }

    public void setWechatLogin(int wechatLogin) {
        this.wechatLogin = wechatLogin;
    }

    public String getWechatNickname() {
        return wechatNickname;
    }

    public void setWechatNickname(String wechatNickname) {
        this.wechatNickname = wechatNickname;
    }

    @Override
    public String toString() {
        String log = "";
        log += "[id:" + getId() + "]";
        log += "[userCode:" + getUserCode() + "]";
        log += "[userName:" + getUserName() + "]";
        log += "[realName:" + getRealName() + "]";
        log += "[balance:" + getBalance() + "]";
        log += "[points:" + getPoints() + "]";
        log += "[password:" + getPassword() + "]";
        log += "[status:" + getStatus() + "]";
        log += "[phone:" + getPhone() + "]";
        log += "[deviceId:" + getDeviceId() + "]";
        log += "[email:" + getEmail() + "]";
        log += "[idCard:" + getIdCard() + "]";
        log += "[sex:" + getSex() + "]";
        log += "[age:" + getAge() + "]";
        log += "[identityAuth:" + getIdentityAuth() + "]";
        log += "[phoneAuth:" + getPhoneAuth() + "]";
        log += "[vehicleAuth:" + getVehicleAuth() + "]";
        log += "[paymentAuth:" + getPaymentAuth() + "]";
        log += "[emailAuth:" + getEmailAuth() + "]";
        log += "[districtCode:" + getDistrictCode() + "]";
        log += "[avatar:" + getAvatar() + "]";
        log += "[driveLicenseId:" + getDriveLicenseId() + "]";
        log += "[idcardFrontsidePic:" + getIdcardFrontsidePic() + "]";
        log += "[idcardBacksidePic:" + getIdcardBacksidePic() + "]";
        log += "[driveLicensePic:" + getDriveLicensePic() + "]";
        log += "[flags:" + getFlags() + "]";
        log += "[applicationId:" + getApplicationId() + "]";
        log += "[utmSource:" + getUtmSource() + "]";
        log += "[utmMedium:" + getUtmMedium() + "]";
        log += "[clientVersion:" + getClientVersion() + "]";
        log += "[registerId:" + getRegisterId() + "]";
        log += "[sendType:" + getSendType() + "]";
        log += "[remark:" + getRemark() + "]";
        log += "[createTime:" + getCreateTime() + "]";
        log += "[updateTime:" + getUpdateTime() + "]";
        return log;
    }
}
