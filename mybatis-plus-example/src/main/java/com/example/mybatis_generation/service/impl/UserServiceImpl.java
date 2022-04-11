package com.example.mybatis_generation.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.mybatis_generation.domain.User;
import com.example.mybatis_generation.dao.UserMapper;
import com.example.mybatis_generation.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiDing
 * @since 2022-04-11
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public boolean addUser(User user) {
        return this.save(user);
    }

    @Override
    public boolean delUserById(Long userId) {
        return this.removeById(userId);
    }

    @Override
    public boolean delUser(String account) {
        QueryWrapper<User> eq = new QueryWrapper<User>()
                // 添加删除条件
                .eq("account", account);
        return this.remove(eq);
    }

    @Override
    public boolean updUser(User user) {
        StrUtil.isNotBlank(user.getPassword());
        UpdateWrapper<User> set = new UpdateWrapper<User>()
                .eq("user_id", user.getUserId())
                // 当传入的密码不为空，则更新
                .set(StrUtil.isNotBlank(user.getPassword()), "password", user.getPassword())
                // 当传入的用户名不为空，则更新
                .set(StrUtil.isNotBlank(user.getUsername()), "username", user.getUsername())
                // 更新时间
                .set("updated_date", new Date());
        return this.update(set);
    }

    @Override
    public List<User> listUser(User user) {
        QueryWrapper<User> eq = new QueryWrapper<User>()
                // 当用户名不为空，该条件生效，执行模糊查询
                .like(StrUtil.isNotBlank(user.getUsername()), "username", user.getUsername())
                // 当用户账号不为空，该条件生效
                .eq(StrUtil.isNotBlank(user.getAccount()), "account", user.getAccount());
        return this.list(eq);
    }
}
