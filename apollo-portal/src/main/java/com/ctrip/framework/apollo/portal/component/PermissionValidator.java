package com.ctrip.framework.apollo.portal.component;

import com.ctrip.framework.apollo.common.entity.AppNamespace;
import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.constant.PermissionType;
import com.ctrip.framework.apollo.portal.service.AppNamespaceService;
import com.ctrip.framework.apollo.portal.service.RolePermissionService;
import com.ctrip.framework.apollo.portal.service.SystemRoleManagerService;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import com.ctrip.framework.apollo.portal.util.RoleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("permissionValidator")
public class PermissionValidator {

  private final UserInfoHolder userInfoHolder;
  private final RolePermissionService rolePermissionService;
  private final PortalConfig portalConfig;
  private final AppNamespaceService appNamespaceService;
  private final SystemRoleManagerService systemRoleManagerService;

  @Autowired
  public PermissionValidator(
          final UserInfoHolder userInfoHolder,
          final RolePermissionService rolePermissionService,
          final PortalConfig portalConfig,
          final AppNamespaceService appNamespaceService,
          final SystemRoleManagerService systemRoleManagerService) {
    this.userInfoHolder = userInfoHolder;
    this.rolePermissionService = rolePermissionService;
    this.portalConfig = portalConfig;
    this.appNamespaceService = appNamespaceService;
    this.systemRoleManagerService = systemRoleManagerService;
  }

  /**
   * 有没有对应 ModifyNamespace 的权限
   *
   * @param appId
   * @param namespaceName
   * @return
   */
  public boolean hasModifyNamespacePermission(String appId, String namespaceName) {
    return rolePermissionService.userHasPermission(userInfoHolder.getUser().getUserId(),
        PermissionType.MODIFY_NAMESPACE,
        RoleUtils.buildNamespaceTargetId(appId, namespaceName));
  }

  // 有没有对应 ModifyNamespace 的权限
  public boolean hasModifyNamespacePermission(String appId, String namespaceName, String env) {
    return hasModifyNamespacePermission(appId, namespaceName) ||
        rolePermissionService.userHasPermission(userInfoHolder.getUser().getUserId(),
            PermissionType.MODIFY_NAMESPACE, RoleUtils.buildNamespaceTargetId(appId, namespaceName, env));
  }

  // 有没有对应 ReleaseNamespace 的权限
  public boolean hasReleaseNamespacePermission(String appId, String namespaceName) {
    return rolePermissionService.userHasPermission(userInfoHolder.getUser().getUserId(),
        PermissionType.RELEASE_NAMESPACE,
        RoleUtils.buildNamespaceTargetId(appId, namespaceName));
  }

  /**
   * 校验有没有 ReleaseNamespace 的权限
   *
   * @param appId
   * @param namespaceName
   * @param env
   * @return
   */
  public boolean hasReleaseNamespacePermission(String appId, String namespaceName, String env) {
    return hasReleaseNamespacePermission(appId, namespaceName) ||
        rolePermissionService.userHasPermission(userInfoHolder.getUser().getUserId(),
        PermissionType.RELEASE_NAMESPACE, RoleUtils.buildNamespaceTargetId(appId, namespaceName, env));
  }

  // 有没有删除 DeleteNamespace 的权限
  public boolean hasDeleteNamespacePermission(String appId) {
    return hasAssignRolePermission(appId) || isSuperAdmin();
  }

  // 有没有操作 Namespace 的权限
  public boolean hasOperateNamespacePermission(String appId, String namespaceName) {
    return hasModifyNamespacePermission(appId, namespaceName) || hasReleaseNamespacePermission(appId, namespaceName);
  }

  // 有没有操作 Namespace 的权限
  public boolean hasOperateNamespacePermission(String appId, String namespaceName, String env) {
    return hasOperateNamespacePermission(appId, namespaceName) ||
        hasModifyNamespacePermission(appId, namespaceName, env) ||
        hasReleaseNamespacePermission(appId, namespaceName, env);
  }

  // 有没有操作 授权用户角色 的权限
  public boolean hasAssignRolePermission(String appId) {
    return rolePermissionService.userHasPermission(userInfoHolder.getUser().getUserId(),
        PermissionType.ASSIGN_ROLE,
        appId);
  }

  // 有没有创建 CreateNamespace 权限
  public boolean hasCreateNamespacePermission(String appId) {

    return rolePermissionService.userHasPermission(userInfoHolder.getUser().getUserId(),
        PermissionType.CREATE_NAMESPACE,
        appId);
  }

  // 有没有创建 CreateNamespace 权限
  public boolean hasCreateAppNamespacePermission(String appId, AppNamespace appNamespace) {

    boolean isPublicAppNamespace = appNamespace.isPublic();

    if (portalConfig.canAppAdminCreatePrivateNamespace() || isPublicAppNamespace) {
      return hasCreateNamespacePermission(appId);
    }

    return isSuperAdmin();
  }

  // 有没有创建 CreateCluster 权限
  public boolean hasCreateClusterPermission(String appId) {
    return rolePermissionService.userHasPermission(userInfoHolder.getUser().getUserId(),
        PermissionType.CREATE_CLUSTER,
        appId);
  }

  // 是否为对应 app 的管理员
  public boolean isAppAdmin(String appId) {
    return isSuperAdmin() || hasAssignRolePermission(appId);
  }

  // 是否为超管
  public boolean isSuperAdmin() {
    return rolePermissionService.isSuperAdmin(userInfoHolder.getUser().getUserId());
  }

  // 是否对当前用户隐藏 Config
  public boolean shouldHideConfigToCurrentUser(String appId, String env, String namespaceName) {
    // 1. check whether the current environment enables member only function
    if (!portalConfig.isConfigViewMemberOnly(env)) {
      return false;
    }

    // 2. public namespace is open to every one
    AppNamespace appNamespace = appNamespaceService.findByAppIdAndName(appId, namespaceName);
    if (appNamespace != null && appNamespace.isPublic()) {
      return false;
    }

    // 3. check app admin and operate permissions
    return !isAppAdmin(appId) && !hasOperateNamespacePermission(appId, namespaceName, env);
  }

  // 有没有创建 Application 的权限
  public boolean hasCreateApplicationPermission() {
    return hasCreateApplicationPermission(userInfoHolder.getUser().getUserId());
  }

  // 有没有创建 Application 的权限
  public boolean hasCreateApplicationPermission(String userId) {
    return systemRoleManagerService.hasCreateApplicationPermission(userId);
  }

  // 有没有管理 APp 的权限
  public boolean hasManageAppMasterPermission(String appId) {
    // the manage app master permission might not be initialized, so we need to check isSuperAdmin first
    return isSuperAdmin() ||
            systemRoleManagerService.hasManageAppMasterPermission(userInfoHolder.getUser().getUserId(), appId);
  }
}
