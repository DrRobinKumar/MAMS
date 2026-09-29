package com.kristalball.mams.service;

import com.kristalball.mams.model.AppUser;
import com.kristalball.mams.model.Role;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// Small helper to know who is logged in right now.
// Used by controllers for role based and base based access.
@Service
public class CurrentUserService {

    public AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isAdmin() {
        return getCurrentUser().getRole() == Role.ADMIN;
    }

    // Admin can see any base (null means all bases).
    // Other users can only see their own base, so we ignore what they requested.
    public Long getBaseIdToUse(Long requestedBaseId) {
        if (isAdmin()) {
            return requestedBaseId;
        }
        return getCurrentUser().getBase().getId();
    }
}
