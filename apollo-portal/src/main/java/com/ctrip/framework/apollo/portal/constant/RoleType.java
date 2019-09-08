package com.ctrip.framework.apollo.portal.constant;

/**
 * 角色的类型
 */
public class RoleType {

  /**
   * 管理员角色
   */
  public static final String MASTER = "Master";

  /**
   * Namespace 修改的角色
   */
  public static final String MODIFY_NAMESPACE = "ModifyNamespace";

  /**
   * Namespace 发布的角色
   */
  public static final String RELEASE_NAMESPACE = "ReleaseNamespace";

  /**
   * 是否为有效的角色类型
   *
   * @param roleType
   * @return
   */
  public static boolean isValidRoleType(String roleType) {
    return MASTER.equals(roleType) || MODIFY_NAMESPACE.equals(roleType) || RELEASE_NAMESPACE.equals(roleType);
  }

}
