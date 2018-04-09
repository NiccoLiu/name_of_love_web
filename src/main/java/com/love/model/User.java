package com.love.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 实体类
 * 
 * @author generator
 */
@TableName(value = "user")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    // columns START

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 微信唯一标识
     */
    private String openid;

    /**
     * 用户注册来源(判断推荐奖)
     */
    private String source;

    /**
     * 账户余额
     */
    private java.math.BigDecimal balance;

    /**
     * 微信名
     */
    private String name;

    /**
     * 微信头像
     */
    @TableField(value = "image_url")
    private String imageUrl;

    /**
     * 是否会员(0否,1是)
     */
    @TableField(value = "old_member")
    private Integer oldMember;

    /**
     * 步数
     */
    @TableField(value = "step_number")
    private Long stepNumber;

    /**
     * 累计返现
     */
    @TableField(value = "cash_back")
    private java.math.BigDecimal cashBack;

    /**
     * 分享奖励
     */
    @TableField(value = "cash_share")
    private java.math.BigDecimal cashShare;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 会员等级
     */
    private Integer grade;
    /**
     * 是否关注
     */
    private Integer subscribe;
    // columns END

    @Override
    protected Serializable pkVal() {
        return id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public java.math.BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(java.math.BigDecimal balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getOldMember() {
        return oldMember;
    }

    public void setOldMember(Integer oldMember) {
        this.oldMember = oldMember;
    }

    public Long getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Long stepNumber) {
        this.stepNumber = stepNumber;
    }

    public java.math.BigDecimal getCashBack() {
        return cashBack;
    }

    public void setCashBack(java.math.BigDecimal cashBack) {
        this.cashBack = cashBack;
    }

    public java.math.BigDecimal getCashShare() {
        return cashShare;
    }

    public void setCashShare(java.math.BigDecimal cashShare) {
        this.cashShare = cashShare;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    @Override
    public String toString() {
        String log = "";
        log += "[id:" + getId() + "]";
        log += "[openid:" + getOpenid() + "]";
        log += "[source:" + getSource() + "]";
        log += "[balance:" + getBalance() + "]";
        log += "[name:" + getName() + "]";
        log += "[imageUrl:" + getImageUrl() + "]";
        log += "[oldMember:" + getOldMember() + "]";
        log += "[stepNumber:" + getStepNumber() + "]";
        log += "[cashBack:" + getCashBack() + "]";
        log += "[cashShare:" + getCashShare() + "]";
        log += "[createTime:" + getCreateTime() + "]";
        log += "[grade:" + getGrade() + "]";
        log += "[subscribe:" + getSubscribe() + "]";
        return log;
    }
}
