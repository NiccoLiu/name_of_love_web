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
@TableName(value = "sign")
public class Sign extends Model<Sign> {

    private static final long serialVersionUID = 1L;

    // columns START

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 微信唯一id
     */
    private String openid;

    /**
     * 签到时间
     */
    @TableField(value = "create_time")
    private Date createTime;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        String log = "";
        log += "[id:" + getId() + "]";
        log += "[openid:" + getOpenid() + "]";
        log += "[createTime:" + getCreateTime() + "]";
        return log;
    }
}
