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
@TableName(value = "order_detail")
public class OrderDetail extends Model<OrderDetail> {

    private static final long serialVersionUID = 1L;

    // columns START

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 充值金额
     */
    private java.math.BigDecimal amount;

    /**
     * 微信唯一标识
     */
    private String openid;

    /**
     * 充值流水号
     */
    @TableField(value = "serial_number")
    private String serialNumber;

    /**
     * 微信支付流水号
     */
    @TableField(value = "wechat_number")
    private String wechatNumber;

    /**
     * 付款银行代号
     */
    @TableField(value = "bank_type")
    private String bankType;

    /**
     * 充值时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 支付时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * 支付结果(0待支付,1成功,2失败)
     */
    @TableField(value = "pay_result")
    private Integer payResult;

    /**
     * 支付类型(1充值,2提现)
     */
    @TableField(value = "pay_type")
    private Integer payType;
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

    public java.math.BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(java.math.BigDecimal amount) {
        this.amount = amount;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getWechatNumber() {
        return wechatNumber;
    }

    public void setWechatNumber(String wechatNumber) {
        this.wechatNumber = wechatNumber;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPayResult() {
        return payResult;
    }

    public void setPayResult(Integer payResult) {
        this.payResult = payResult;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    @Override
    public String toString() {
        String log = "";
        log += "[id:" + getId() + "]";
        log += "[amount:" + getAmount() + "]";
        log += "[openid:" + getOpenid() + "]";
        log += "[serialNumber:" + getSerialNumber() + "]";
        log += "[wechatNumber:" + getWechatNumber() + "]";
        log += "[bankType:" + getBankType() + "]";
        log += "[createTime:" + getCreateTime() + "]";
        log += "[endTime:" + getEndTime() + "]";
        log += "[payResult:" + getPayResult() + "]";
        log += "[payType:" + getPayType() + "]";
        return log;
    }
}
