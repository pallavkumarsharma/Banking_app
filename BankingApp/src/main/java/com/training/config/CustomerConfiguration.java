package com.training.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.training.filters.JwtFilter;
import com.training.service.BankUserDetailsService;


@Configuration
@EnableWebSecurity
public class CustomerConfiguration extends WebSecurityConfigurerAdapter{

	@Autowired 
	private BankUserDetailsService bankUserDetailsService;

	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new PasswordEncoder()
		{
			@Override
			public String encode(CharSequence userEntered) {
				return userEntered.toString();
			}

			@Override
			public boolean matches(CharSequence userEntered, String password) {
				System.out.println("Username Entered: "+userEntered);
				System.out.println("Password Entered: "+password);
				if(password.equals(userEntered))   
					return true;
				return false;
			}
		};

	}

	//authentication
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(bankUserDetailsService)
		.passwordEncoder(passwordEncoder());
	}

	//authorization
	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {

		httpSecurity
		.csrf()
		.disable()
		.authorizeRequests()
		.antMatchers("/authenticate")
		.permitAll()
		.and()
		.authorizeRequests()
		.antMatchers("/customer/**")
		.hasAnyRole("CUSTOMER","ADMIN")
		.and()
		.authorizeRequests()
		.antMatchers("/admin/**")
		.hasRole("ADMIN")
		.anyRequest()
		.authenticated()
		;

		httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
}
