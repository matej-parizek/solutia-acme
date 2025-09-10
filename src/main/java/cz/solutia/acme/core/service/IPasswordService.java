package cz.solutia.acme.core.service;

public interface IPasswordService {
    void changePassword(String username, String currentPassword, String newPassword);
}
