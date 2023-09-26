package com.springboot.smartcontact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class MyConfig {

	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

		daoAuthenticationProvider.setUserDetailsService(getUserDetailService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;
	}
	
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
 }

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize.
				requestMatchers(new AntPathRequestMatcher("/user/**")).hasRole("USER").
				requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
				.anyRequest().authenticated()) 
		                                 //custom page url
				.formLogin(form -> form.loginPage("/signin").defaultSuccessUrl("/user/index"))
				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll());
				
		return http.build();

	}

	 
//	 protected void configure(HttpSecurity http) throws Exception {
//	        http.authorizeRequests().requestMatchers("/admin/**").hasRole("ADMIN")
//	        .requestMatchers("/user/**").hasRole("USER")
//	        .requestMatchers("/**").permitAll().and().formLogin().and().csrf().disable();
//	 }

}
