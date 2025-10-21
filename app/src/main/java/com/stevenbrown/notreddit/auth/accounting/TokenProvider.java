package com.stevenbrown.notreddit.auth.accounting;

import androidx.annotation.Nullable;

public interface TokenProvider {
    @Nullable
    String getCurrentAccessToken();
}
