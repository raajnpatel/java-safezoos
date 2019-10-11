package com.lambdaschool.zoos.service;

import com.lambdaschool.zoos.model.Role;
import com.lambdaschool.zoos.repository.RoleRepository;
import com.lambdaschool.zoos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service(value = "roleService")
public class RoleServiceImpl implements RoleService
{
    @Autowired
    RoleRepository rolerepos;

    @Autowired
    UserRepository userrepos;

    @Override
    public List<Role> findAll()
    {
        List<Role> list = new ArrayList<>();
        rolerepos.findAll()
                 .iterator()
                 .forEachRemaining(list::add);
        return list;
    }


    @Override
    public Role findRoleById(long id)
    {
        return rolerepos.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Role id " + id + " not found!"));
    }


    @Transactional
    @Override
    public void delete(long id)
    {
        rolerepos.findById(id)
                 .orElseThrow(() -> new EntityNotFoundException("Role id " + id + " not found!"));
        rolerepos.deleteById(id);
    }


    @Transactional
    @Override
    public Role save(Role role)
    {
        Role newRole = new Role();
        newRole.setName(role.getName());
        if (role.getUserroles()
                .size() > 0)
        {
            throw new EntityNotFoundException("User Roles are not updated through Role. See endpoint POST: users/user/{userid}/role/{roleid}");
        }

        return rolerepos.save(role);
    }
}
