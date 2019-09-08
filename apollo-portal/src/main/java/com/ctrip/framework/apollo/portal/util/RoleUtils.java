package com.ctrip.framework.apollo.portal.util;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.portal.constant.RoleType;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.Iterator;

/**
 * 角色工具类
 */
public class RoleUtils {

  // StringJoiner， 连接器， 连接符 +
  private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).skipNulls();
  // StringSpliter, 分割器， 分隔符 +
  private static final Splitter STRING_SPLITTER = Splitter.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR)
      .omitEmptyStrings().trimResults();

  /**
   * App Master角色名称： Master+appid
   *
   * @param appId
   * @return
   */
  public static String buildAppMasterRoleName(String appId) {
    return STRING_JOINER.join(RoleType.MASTER, appId);
  }

  /**
   * 从 Master role name 中提取出 AppId
   * 跳过角色类型的字段， Master, 提取出
   *
   * @param masterRoleName
   * @return
   */
  public static String extractAppIdFromMasterRoleName(String masterRoleName) {
    Iterator<String> parts = STRING_SPLITTER.split(masterRoleName).iterator();

    // skip role type
    if (parts.hasNext() && parts.next().equals(RoleType.MASTER) && parts.hasNext()) {
      return parts.next();
    }

    return null;
  }

  /**
   * 从 role name 中提取出 AppId
   *
   * @param roleName
   * @return
   */
  public static String extractAppIdFromRoleName(String roleName) {
     Iterator<String> parts = STRING_SPLITTER.split(roleName).iterator();
     if (parts.hasNext()) {
       String roleType = parts.next();
       if (RoleType.isValidRoleType(roleType) && parts.hasNext()) {
         return parts.next();
       }
     }
     return null;
  }

  /**
   * app role name
   * roleType+appid
   *
   * @param appId
   * @param roleType
   * @return
   */
  public static String buildAppRoleName(String appId, String roleType) {
    return STRING_JOINER.join(roleType, appId);
  }

  /**
   * 创建角色的名称
   *
   * @param appId
   * @param namespaceName
   * @return
   */
  public static String buildModifyNamespaceRoleName(String appId, String namespaceName) {
    return buildModifyNamespaceRoleName(appId, namespaceName, null);
  }

  /**
   * ModifyNamespace+aapid+namespace+env, 使用 + 进行连接
   *
   * @param appId
   * @param namespaceName
   * @param env
   * @return
   */
  public static String buildModifyNamespaceRoleName(String appId, String namespaceName, String env) {
    return STRING_JOINER.join(RoleType.MODIFY_NAMESPACE, appId, namespaceName, env);
  }

  public static String buildModifyDefaultNamespaceRoleName(String appId) {
    return STRING_JOINER.join(RoleType.MODIFY_NAMESPACE, appId, ConfigConsts.NAMESPACE_APPLICATION);
  }

  public static String buildReleaseNamespaceRoleName(String appId, String namespaceName) {
    return buildReleaseNamespaceRoleName(appId, namespaceName, null);
  }

  /**
   * ReleaseNamespace+aapid+namespace+env, 使用 + 进行连接
   * @param appId
   * @param namespaceName
   * @return
   */
  public static String buildReleaseNamespaceRoleName(String appId, String namespaceName, String env) {
    return STRING_JOINER.join(RoleType.RELEASE_NAMESPACE, appId, namespaceName, env);
  }

  public static String buildNamespaceRoleName(String appId, String namespaceName, String roleType) {
    return buildNamespaceRoleName(appId, namespaceName, roleType, null);
  }

  /**
   * Namespace role name
   *
   * @param appId
   * @param namespaceName
   * @param roleType
   * @param env
   * @return
   */
  public static String buildNamespaceRoleName(String appId, String namespaceName, String roleType, String env) {
    return STRING_JOINER.join(roleType, appId, namespaceName, env);
  }

  public static String buildReleaseDefaultNamespaceRoleName(String appId) {
    return STRING_JOINER.join(RoleType.RELEASE_NAMESPACE, appId, ConfigConsts.NAMESPACE_APPLICATION);
  }

  /**
   * 创建 Namespace 的 targetid
   *
   * @param appId
   * @param namespaceName
   * @return
   */
  public static String buildNamespaceTargetId(String appId, String namespaceName) {
    return buildNamespaceTargetId(appId, namespaceName, null);
  }

  /**
   * 创建对应的 namespace targetid
   * appid+namespaceName+env
   *
   * @param appId
   * @param namespaceName
   * @param env
   * @return
   */
  public static String buildNamespaceTargetId(String appId, String namespaceName, String env) {
    return STRING_JOINER.join(appId, namespaceName, env);
  }

  public static String buildDefaultNamespaceTargetId(String appId) {
    return STRING_JOINER.join(appId, ConfigConsts.NAMESPACE_APPLICATION);
  }

  public static String buildCreateApplicationRoleName(String permissionType, String permissionTargetId) {
    return STRING_JOINER.join(permissionType, permissionTargetId);
  }

  public static String buildManageAppMasterRoleName(String permissionType, String permissionTargetId) {
    return STRING_JOINER.join(permissionType, permissionTargetId);
  }
}
