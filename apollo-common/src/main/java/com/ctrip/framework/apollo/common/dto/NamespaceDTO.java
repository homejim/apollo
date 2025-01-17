package com.ctrip.framework.apollo.common.dto;

import com.ctrip.framework.apollo.common.utils.InputValidator;
import javax.validation.constraints.Pattern;

public class NamespaceDTO extends BaseDTO {
  // id
  private long id;

  // 所属 appid
  private String appId;

  // 对应集群的名称
  private String clusterName;

  @Pattern(
          regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
          message = "Namespace格式错误: " + InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
  )
  private String namespaceName;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAppId() {
    return appId;
  }

  public String getClusterName() {
    return clusterName;
  }

  public String getNamespaceName() {
    return namespaceName;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public void setNamespaceName(String namespaceName) {
    this.namespaceName = namespaceName;
  }
}
