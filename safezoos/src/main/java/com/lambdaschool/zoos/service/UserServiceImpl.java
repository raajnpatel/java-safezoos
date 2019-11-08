package com.lambdaschool.zoos.service;

import com.lambdaschool.zoos.model.Role;
import com.lambdaschool.zoos.model.User;
import com.lambdaschool.zoos.model.UserRoles;
import com.lambdaschool.zoos.repository.RoleRepository;
import com.lambdaschool.zoos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;


@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService,
        UserService
{

    @Autowired
    private UserRepository userrepos;

    @Autowired
    private RoleRepository rolerepos;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userrepos.findByUsername(username.toLowerCase());
        if (user == null)
        {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                user.getAuthority());
    }

    public User findUserById(long id) throws EntityNotFoundException
    {
        return userrepos.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User id " + id + " not found!"));
    }

    @Override
    public List<User> findByNameContaining(String username)
    {
        return userrepos.findByUsernameContainingIgnoreCase(username.toUpperCase());
    }

    @Override
    public List<User> findAll()
    {
        List<User> list = new ArrayList<>();
        userrepos.findAll()
                .iterator()
                .forEachRemaining(list::add);
        return list;
    }

    @Transactional
    @Override
    public void delete(long id)
    {
        userrepos.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User id " + id + " not found!"));
        userrepos.deleteById(id);
    }

    @Override
    public User findByName(String name)
    {
        User uu = userrepos.findByUsername(name.toLowerCase());
        if (uu == null)
        {
            throw new EntityNotFoundException("User name " + name + " not found!");
        }
        return uu;
    }

    @Transactional
    @Override
    public User save(User user)
    {
        if (userrepos.findByUsername(user.getUsername()) != null)
        {
            throw new EntityNotFoundException(user.getUsername() + " is already taken!");
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPasswordNoEncrypt(user.getPassword());

        ArrayList<UserRoles> newRoles = new ArrayList<>();
        for (UserRoles ur : user.getUserroles())
        {
            long id = ur.getRole()
                    .getRoleid();
            Role role = rolerepos.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Role id " + id + " not found!"));
            newRoles.add(new UserRoles(newUser,
                    ur.getRole()));
        }
        newUser.setUserroles(newRoles);

        return userrepos.save(newUser);
    }

    @Transactional
    @Override
    public User update(User user,
                       long id,
                       boolean isAdmin)
    {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        User authenticatedUser = userrepos.findByUsername(authentication.getName());

        if (id == authenticatedUser.getUserid() || isAdmin)
        {
            User currentUser = findUserById(id);

            if (user.getUsername() != null)
            {
                currentUser.setUsername(user.getUsername());
            }

            if (user.getPassword() != null)
            {
                currentUser.setPasswordNoEncrypt(user.getPassword());
            }

            if (user.getUserroles()
                    .size() > 0)
            {
                throw new EntityNotFoundException("User Roles are not updated through User. See endpoint POST: users/user/{userid}/role/{roleid}");
            }

            return userrepos.save(currentUser);
        }
        else
        {
            throw new EntityNotFoundException(id + " Not current user");
        }
    }
}