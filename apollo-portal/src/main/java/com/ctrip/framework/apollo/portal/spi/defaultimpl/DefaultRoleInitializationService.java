package com.ctrip.framework.apollo.portal.spi.defaultimpl;

import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.common.entity.BaseEntity;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.constant.PermissionType;
import com.ctrip.framework.apollo.portal.constant.RoleType;
import com.ctrip.framework.apollo.portal.entity.po.Permission;
import com.ctrip.framework.apollo.portal.entity.po.Role;
import com.ctrip.framework.apollo.portal.repository.PermissionRepository;
import com.ctrip.framework.apollo.portal.service.RoleInitializationService;
import com.ctrip.framework.apollo.portal.service.RolePermissionService;
import com.ctrip.framework.apollo.portal.service.SystemRoleManagerService;
import com.ctrip.framework.apollo.portal.util.RoleUtils;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by timothy on 2017/4/26.
 */
public class DefaultRoleInitializationService implements RoleInitializationService {

  @Autowired
  private RolePermissionService rolePermissionService;
  @Autowired
  private PortalConfig portalConfig;
  @Autowired
  private PermissionRepository permissionRepository;

  @Transactional
  public void initAppRoles(App app) {
    String appId = app.getAppId();

    // 构建对应的 App 管理员角色名称
    String appMasterRoleName = RoleUtils.buildAppMasterRoleName(appId);

    //has created before。 检查是否已经创建
    if (rolePermissionService.findRoleByRoleName(appMasterRoleName) != null) {
      return;
    }

    String operator = app.getDataChangeCreatedBy();
    //create app permissions。 创建对应的 app 权限
    createAppMasterRole(appId, operator);
    //create manageAppMaster permission。
    createManageAppMasterRole(appId, operator);

    //assign master role to user。 授权 master 角色给 app 责任人
    rolePermissionService
        .assignRoleToUsers(RoleUtils.buildAppMasterRoleName(appId), Sets.newHashSet(app.getOwnerName()),
            operator);


    initNamespaceRoles(appId, ConfigConsts.NAMESPACE_APPLICATION, operator);
    initNamespaceEnvRoles(appId, ConfigConsts.NAMESPACE_APPLICATION, operator);

    //assign modify、release namespace role to user。 授权修改/发布的权限给当前的用户
    rolePermissionService.assignRoleToUsers(
        RoleUtils.buildNamespaceRoleName(appId, ConfigConsts.NAMESPACE_APPLICATION, RoleType.MODIFY_NAMESPACE),
        Sets.newHashSet(operator), operator);
    rolePermissionService.assignRoleToUsers(
        RoleUtils.buildNamespaceRoleName(appId, ConfigConsts.NAMESPACE_APPLICATION, RoleType.RELEASE_NAMESPACE),
        Sets.newHashSet(operator), operator);

  }

  /**
   * 初始化 Application 对应的 namespace 角色（修改和发布的权限）
   *
   * @param appId applicationId
   * @param namespaceName namespace 概念
   * @param operator 操作人
   */
  @Transactional
  public void initNamespaceRoles(String appId, String namespaceName, String operator) {

    // 不存在则创建对应的 namespace 修改权限
    String modifyNamespaceRoleName = RoleUtils.buildModifyNamespaceRoleName(appId, namespaceName);
    if (rolePermissionService.findRoleByRoleName(modifyNamespaceRoleName) == null) {
      createNamespaceRole(appId, namespaceName, PermissionType.MODIFY_NAMESPACE,
          modifyNamespaceRoleName, operator);
    }

    // 不存在则创建对应的 namespace 发布权限
    String releaseNamespaceRoleName = RoleUtils.buildReleaseNamespaceRoleName(appId, namespaceName);
    if (rolePermissionService.findRoleByRoleName(releaseNamespaceRoleName) == null) {
      createNamespaceRole(appId, namespaceName, PermissionType.RELEASE_NAMESPACE,
          releaseNamespaceRoleName, operator);
    }
  }

  /**
   * 创建对应的环境角色
   *
   * @param appId
   * @param namespaceName
   * @param operator
   */
  @Transactional
  public void initNamespaceEnvRoles(String appId, String namespaceName, String operator) {
    // 环境信息
    List<Env> portalEnvs = portalConfig.portalSupportedEnvs();

    for (Env env : portalEnvs) {
      // 初始化指定环境的 namespace role
      initNamespaceSpecificEnvRoles(appId, namespaceName, env.toString(), operator);
    }
  }

  @Transactional
  public void initNamespaceSpecificEnvRoles(String appId, String namespaceName, String env, String operator) {
    String modifyNamespaceEnvRoleName = RoleUtils.buildModifyNamespaceRoleName(appId, namespaceName, env);
    // 不存在则创建对应的 ModifyNamespace 权限
    if (rolePermissionService.findRoleByRoleName(modifyNamespaceEnvRoleName) == null) {
      createNamespaceEnvRole(appId, namespaceName, PermissionType.MODIFY_NAMESPACE, env,
          modifyNamespaceEnvRoleName, operator);
    }

    // 不存在则创建对应的 ReleaseNamespace 权限
    String releaseNamespaceEnvRoleName = RoleUtils.buildReleaseNamespaceRoleName(appId, namespaceName, env);
    if (rolePermissionService.findRoleByRoleName(releaseNamespaceEnvRoleName) == null) {
      createNamespaceEnvRole(appId, namespaceName, PermissionType.RELEASE_NAMESPACE, env,
          releaseNamespaceEnvRoleName, operator);
    }
  }

  @Transactional
  public void initCreateAppRole() {
    if (rolePermissionService.findRoleByRoleName(SystemRoleManagerService.CREATE_APPLICATION_ROLE_NAME) != null) {
      return;
    }
    Permission createAppPermission = permissionRepository.findTopByPermissionTypeAndTargetId(PermissionType.CREATE_APPLICATION, SystemRoleManagerService.SYSTEM_PERMISSION_TARGET_ID);
    if (createAppPermission == null) {
      // create application permission init
      createAppPermission = createPermission(SystemRoleManagerService.SYSTEM_PERMISSION_TARGET_ID, PermissionType.CREATE_APPLICATION, "apollo");
      rolePermissionService.createPermission(createAppPermission);
    }
    //  create application role init
    Role createAppRole = createRole(SystemRoleManagerService.CREATE_APPLICATION_ROLE_NAME, "apollo");
    rolePermissionService.createRoleWithPermissions(createAppRole, Sets.newHashSet(createAppPermission.getId()));
  }

  @Transactional
  private void createManageAppMasterRole(String appId, String operator) {
    Permission permission = createPermission(appId, PermissionType.MANAGE_APP_MASTER, operator);
    rolePermissionService.createPermission(permission);
    Role role = createRole(RoleUtils.buildManageAppMasterRoleName(PermissionType.MANAGE_APP_MASTER, appId), operator);
    Set<Long> permissionIds = new HashSet<>();
    permissionIds.add(permission.getId());
    rolePermissionService.createRoleWithPermissions(role, permissionIds);
  }

  // fix historical data
  @Transactional
  public void initManageAppMasterRole(String appId, String operator) {
    String manageAppMasterRoleName = RoleUtils.buildManageAppMasterRoleName(PermissionType.MANAGE_APP_MASTER, appId);
    if (rolePermissionService.findRoleByRoleName(manageAppMasterRoleName) != null) {
      return;
    }
    synchronized (DefaultRoleInitializationService.class) {
      createManageAppMasterRole(appId, operator);
    }
  }

  /**
   * 创建 APP 管理员的角色
   *
   * @param appId
   * @param operator
   */
  private void createAppMasterRole(String appId, String operator) {
    // app 权限： CreateCluster， CreateNamespace， AssignRole
    Set<Permission> appPermissions =
            Stream.of(PermissionType.CREATE_CLUSTER, PermissionType.CREATE_NAMESPACE, PermissionType.ASSIGN_ROLE)
                    .map(permissionType -> createPermission(appId, permissionType, operator)).collect(Collectors.toSet());
    Set<Permission> createdAppPermissions = rolePermissionService.createPermissions(appPermissions);
    Set<Long>
            appPermissionIds =
            createdAppPermissions.stream().map(BaseEntity::getId).collect(Collectors.toSet());

    //create app master role。 根据权限创建对应的 role
    Role appMasterRole = createRole(RoleUtils.buildAppMasterRoleName(appId), operator);

    // 创建角色及其与权限的映射
    rolePermissionService.createRoleWithPermissions(appMasterRole, appPermissionIds);
  }

  /**
   * 创建权限
   *
   * @param targetId
   * @param permissionType
   * @param operator
   * @return
   */
  private Permission createPermission(String targetId, String permissionType, String operator) {
    Permission permission = new Permission();
    permission.setPermissionType(permissionType);
    permission.setTargetId(targetId);
    permission.setDataChangeCreatedBy(operator);
    permission.setDataChangeLastModifiedBy(operator);
    return permission;
  }

  private Role createRole(String roleName, String operator) {
    Role role = new Role();
    role.setRoleName(roleName);
    role.setDataChangeCreatedBy(operator);
    role.setDataChangeLastModifiedBy(operator);
    return role;
  }

  /**
   * 创建角色
   *
   * @param appId appid
   * @param namespaceName
   * @param permissionType
   * @param roleName
   * @param operator
   */
  private void createNamespaceRole(String appId, String namespaceName, String permissionType,
                                   String roleName, String operator) {

    // 构造 Namespace 权限。 权限是 权限id+权限类型
    Permission permission =
            createPermission(RoleUtils.buildNamespaceTargetId(appId, namespaceName), permissionType, operator);
    // 创建权限
    Permission createdPermission = rolePermissionService.createPermission(permission);

    // 构造角色。 角色就一个名称
    Role role = createRole(roleName, operator);
    // 创建角色
    rolePermissionService
        .createRoleWithPermissions(role, Sets.newHashSet(createdPermission.getId()));
  }


  private void createNamespaceEnvRole(String appId, String namespaceName, String permissionType, String env,
                                      String roleName, String operator) {
    Permission permission =
        createPermission(RoleUtils.buildNamespaceTargetId(appId, namespaceName, env), permissionType, operator);
    Permission createdPermission = rolePermissionService.createPermission(permission);

    Role role = createRole(roleName, operator);
    rolePermissionService
        .createRoleWithPermissions(role, Sets.newHashSet(createdPermission.getId()));
  }
}
