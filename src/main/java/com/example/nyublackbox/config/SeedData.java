package com.example.nyublackbox.config;

import com.example.nyublackbox.models.Account;
import com.example.nyublackbox.models.Authority;
import com.example.nyublackbox.models.Post;
import com.example.nyublackbox.repositories.AuthorityRepository;
import com.example.nyublackbox.services.AccountService;
import com.example.nyublackbox.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    private PostService postService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Post> posts = postService.getAll();

        if (posts.size() == 0) {

            Authority user = new Authority();
            user.setName("ROLE_USER");
            authorityRepository.save(user);

            Authority admin = new Authority();
            admin.setName("ROLE_ADMIN");
            authorityRepository.save(admin);

            Account account1 = new Account();
            Account account2 = new Account();

            account1.setFirstName("user_first");
            account1.setLastName("user_last");
            account1.setEmail("user.user@domain.com");
            account1.setPassword("password");
            Set<Authority> authorities1 = new HashSet<>();
            authorityRepository.findById("ROLE_USER").ifPresent(authorities1::add);
            account1.setAuthorities(authorities1);


            account2.setFirstName("admin_first");
            account2.setLastName("admin_last");
            account2.setEmail("admin.admin@domain.com");
            account2.setPassword("password");

            Set<Authority> authorities2 = new HashSet<>();
            authorityRepository.findById("ROLE_ADMIN").ifPresent(authorities2::add);
            authorityRepository.findById("ROLE_USER").ifPresent(authorities2::add);
            account2.setAuthorities(authorities2);

            accountService.save(account1);
            accountService.save(account2);

            Post post1 = new Post();
            post1.setTitle("NYU Clery Campus Notice - Robbery");
            post1.setUserUpdate("Completed");
            post1.setAdminUpdate("Completed");
            post1.setDepartment("NYU Courant");
            post1.setBody("The complainant, an NYU student, reported that they were returning to retrieve their electric scooter from the rack in Schwartz Plaza when they happened upon an unidentified individual attempting to break the lock and take the scooter. When the complainant confronted the individual, the individual said the complainant should stay back, claiming they had a weapon. The individual then broke the lock and left with the scooter. No injuries were reported.");
            post1.setAccount(account1);

            Post post2 = new Post();
            post2.setTitle("Clery Campus Notice - Aggravated Assault");
            post2.setUserUpdate("Completed");
            post2.setAdminUpdate("Completed");
            post2.setDepartment("NYU Courant");
            post2.setBody("NYPD reported that a victim had been slashed by an unknown person; at this point, it is unclear whether the victim has any connection to NYU. The person was taken to a hospital by EMS. NYPD does not believe this incident was related to the shots fired incident reported earlier today. ");
            post2.setAccount(account2);

            postService.save(post1);
            postService.save(post2);
        }
    }

}
