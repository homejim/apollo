package com.ctrip.framework.apollo.common.entity;

import com.ctrip.framework.apollo.common.utils.InputValidator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 核心概念中的； application 实体类
 */
@Entity
@Table(name = "App")
@SQLDelete(sql = "Update App set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class App extends BaseEntity {

  /**
   * application 名称
   */
  @NotBlank(message = "Name cannot be blank")
  @Column(name = "Name", nullable = false)
  private String name;

  /**
   * application 编号， 有校验规则， 需要是字母， 数字和 _-.
   */
  @NotBlank(message = "AppId cannot be blank")
  @Pattern(
      regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
      message = InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
  )
  @Column(name = "AppId", nullable = false)
  private String appId;

  /**
   * 组织 id
   */
  @Column(name = "OrgId", nullable = false)
  private String orgId;

  /**
   * 组织名称
   */
  @Column(name = "OrgName", nullable = false)
  private String orgName;

  /**
   * 项目负责人的名称： 在创建时选的
   */
  @NotBlank(message = "OwnerName cannot be blank")
  @Column(name = "OwnerName", nullable = false)
  private String ownerName;

  /**
   * 项目负责人的邮箱
   */
  @NotBlank(message = "OwnerEmail cannot be blank")
  @Column(name = "OwnerEmail", nullable = false)
  private String ownerEmail;

  public String getAppId() {
    return appId;
  }

  public String getName() {
    return name;
  }

  public String getOrgId() {
    return orgId;
  }

  public String getOrgName() {
    return orgName;
  }

  public String getOwnerEmail() {
    return ownerEmail;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public void setOwnerEmail(String ownerEmail) {
    this.ownerEmail = ownerEmail;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String toString() {
    return toStringHelper().add("name", name).add("appId", appId)
        .add("orgId", orgId)
        .add("orgName", orgName)
        .add("ownerName", ownerName)
        .add("ownerEmail", ownerEmail).toString();
  }

  /**
   * 建造者模式
   */
  public static class Builder {

    public Builder() {
    }

    private App app = new App();

    public Builder name(String name) {
      app.setName(name);
      return this;
    }

    public Builder appId(String appId) {
      app.setAppId(appId);
      return this;
    }

    public Builder orgId(String orgId) {
      app.setOrgId(orgId);
      return this;
    }

    public Builder orgName(String orgName) {
      app.setOrgName(orgName);
      return this;
    }

    public Builder ownerName(String ownerName) {
      app.setOwnerName(ownerName);
      return this;
    }

    public Builder ownerEmail(String ownerEmail) {
      app.setOwnerEmail(ownerEmail);
      return this;
    }

    public App build() {
      return app;
    }

  }

  public static Builder builder() {
    return new Builder();
  }


}
