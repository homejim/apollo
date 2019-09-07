package com.ctrip.framework.apollo.common.entity;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

/**
 * 实体基类， 加 @MappedSuperclass 注解不会映射成数据库表。
 * 这是一种规约， 表明作者想将多数的数据库表都含有这些字段， 以便在编码的时候可以进行一些方便的操作。
 * 字段都是很基础的字段， 可参考阿里手册中的规范
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseEntity {

  /**
   * 主键， 自增
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "Id")
  private long id;

  /**
   * 是否删除
   */
  @Column(name = "IsDeleted", columnDefinition = "Bit default '0'")
  protected boolean isDeleted = false;

  /**
   * 数据变更创建人
   */
  @Column(name = "DataChange_CreatedBy", nullable = false)
  private String dataChangeCreatedBy;

  /**
   * 数据变更创建时间
   */
  @Column(name = "DataChange_CreatedTime", nullable = false)
  private Date dataChangeCreatedTime;

  /**
   * 数据最后更新人
   */
  @Column(name = "DataChange_LastModifiedBy")
  private String dataChangeLastModifiedBy;

  /**
   * 数据最后更新时间
   */
  @Column(name = "DataChange_LastTime")
  private Date dataChangeLastModifiedTime;

  public String getDataChangeCreatedBy() {
    return dataChangeCreatedBy;
  }

  public Date getDataChangeCreatedTime() {
    return dataChangeCreatedTime;
  }

  public String getDataChangeLastModifiedBy() {
    return dataChangeLastModifiedBy;
  }

  public Date getDataChangeLastModifiedTime() {
    return dataChangeLastModifiedTime;
  }

  public long getId() {
    return id;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDataChangeCreatedBy(String dataChangeCreatedBy) {
    this.dataChangeCreatedBy = dataChangeCreatedBy;
  }

  public void setDataChangeCreatedTime(Date dataChangeCreatedTime) {
    this.dataChangeCreatedTime = dataChangeCreatedTime;
  }

  public void setDataChangeLastModifiedBy(String dataChangeLastModifiedBy) {
    this.dataChangeLastModifiedBy = dataChangeLastModifiedBy;
  }

  public void setDataChangeLastModifiedTime(Date dataChangeLastModifiedTime) {
    this.dataChangeLastModifiedTime = dataChangeLastModifiedTime;
  }

  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
  }

  public void setId(long id) {
    this.id = id;
  }

  /**
   * 保存前执行的方法
   */
  @PrePersist
  protected void prePersist() {
    if (this.dataChangeCreatedTime == null) dataChangeCreatedTime = new Date();
    if (this.dataChangeLastModifiedTime == null) dataChangeLastModifiedTime = new Date();
  }

  /**
   * 更新前执行的方法
   */
  @PreUpdate
  protected void preUpdate() {
    this.dataChangeLastModifiedTime = new Date();
  }

  /**
   * 删除前执行的方法
   */
  @PreRemove
  protected void preRemove() {
    this.dataChangeLastModifiedTime = new Date();
  }

  protected ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(this).omitNullValues().add("id", id)
        .add("dataChangeCreatedBy", dataChangeCreatedBy)
        .add("dataChangeCreatedTime", dataChangeCreatedTime)
        .add("dataChangeLastModifiedBy", dataChangeLastModifiedBy)
        .add("dataChangeLastModifiedTime", dataChangeLastModifiedTime);
  }

  public String toString(){
    return toStringHelper().toString();
  }
}
