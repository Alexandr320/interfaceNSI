package nii.ps.interfaceNSI.service;

import nii.ps.interfaceNSI.dao.TableDao;
import nii.ps.interfaceNSI.model.TableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private TableDao tableDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TableModel tableModel = tableDao.getTableModelForSql(
                "select * from getUserDls(:user_id);",
                "user_id", Integer.valueOf(username)
        );
        if (tableModel.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        return User.withDefaultPasswordEncoder()
                .username(username)
                .password("1234")
                .authorities("ADMIN")
                .build();
    }
}