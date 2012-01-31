package com.imcode.imcms.servlet;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

// todo: synchronize
public class PasswordReset extends HttpServlet {

    static final int SALT_LENGTH = 16;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String pwd = request.getParameter("pwd");
        String pwdRetype = request.getParameter("pwdRetype");

        if (id != null) {
            if (pwd != null && pwdRetype != null) {
                // check strength..., etc
                // change password
                // remove change password link form the db
                // redirect to login page
            }
        }
    }



    public void generatePasswordHash(String password) throws Exception {
        SecureRandom sr = new SecureRandom();
        byte salt[] = sr.generateSeed(SALT_LENGTH);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 2048, 160);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        byte[] saltFollowedByHash = ArrayUtils.addAll(salt, hash);

        Hex.encodeHexString(saltFollowedByHash);
    }

    public String encryptPassword(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 2048, 160);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        byte[] saltFollowedByHash = ArrayUtils.addAll(salt, hash);

        return Hex.encodeHexString(saltFollowedByHash);
    }


    public boolean validatePassword(String password, String encryptedPassword) throws Exception {
        byte[] saltFollowedByHash = Hex.decodeHex(encryptedPassword.toCharArray());
        byte[] salt = ArrayUtils.subarray(saltFollowedByHash, 0, SALT_LENGTH);

        return encryptPassword(password, salt).equals(encryptedPassword);
    }
}
