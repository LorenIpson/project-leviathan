package game_forum_api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 表示這個註解只能標記在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckMemberRole {
	int value(); // 設定所需的權限列表
}