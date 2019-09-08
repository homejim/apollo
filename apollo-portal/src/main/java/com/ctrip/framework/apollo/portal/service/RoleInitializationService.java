package com.ctrip.framework.apollo.portal.service;

import com.ctrip.framework.apollo.common.entity.App;

public interface RoleInitializationService {

  /**
   * 初始化 App role
   * @param app
   */
  public void initAppRoles(App app);

  /**
   * 初始化 Namespace role
   *
   * @param appId
   * @param namespaceName
   * @param operator
   */
  public void initNamespaceRoles(String appId, String namespaceName, String operator);

  /**
   * 初始化 Namespace env role
   *
   * @param appId
   * @param namespaceName
   * @param operator
   */
  public void initNamespaceEnvRoles(String appId, String namespaceName, String operator);

  /**
   * 初始化 Namespace 特定环境 role
   *
   * @param appId
   * @param namespaceName
   * @param env
   * @param operator
   */
  public void initNamespaceSpecificEnvRoles(String appId, String namespaceName, String env, String operator);

  /**
   * 初始化创建 AppRole
   */
  public void initCreateAppRole();

  /**
   * 创建管理 App 的角色
   * 、
   * @param appId
   * @param operator
   */
  public void initManageAppMasterRole(String appId, String operator);

}
