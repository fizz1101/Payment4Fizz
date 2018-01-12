package com.fizz.common.entity;

import com.fizz.common.utils.IdWorker;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 基础实体类
 * Created by fuzhongyu on 2017/7/21.
 */
public class BaseEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    protected String id;

    protected Boolean isNewRecord=false;  //标识是否是新记录

    /**
     * 自定义SQL（SQL标识，SQL内容）
     */
    protected Map<String, String> sqlMap;

    public void preInsert(){
        if (!this.isNewRecord){
            setId(IdWorker.snowflakeId());
        }
    }

    public void preUpdate(){

    }

    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     * @return
     */
    public boolean getIsNewRecord() {
        return isNewRecord || StringUtils.isNoneBlank(getId());
    }


    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     */
    public void setIsNewRecord(boolean isNewRecord) {
        this.isNewRecord = isNewRecord;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

}
