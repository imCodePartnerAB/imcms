package imcode.server.kerberos;

import com.imcode.imcms.api.ContentManagementSystem;

public class KerberosLoginResult {
    private KerberosLoginStatus status;
    private ContentManagementSystem contentManagementSystem;

    public KerberosLoginResult(KerberosLoginStatus status) {
        this.status = status;
    }

    public ContentManagementSystem getContentManagementSystem() {
        return contentManagementSystem;
    }

    public void setContentManagementSystem(ContentManagementSystem contentManagementSystem) {
        this.contentManagementSystem = contentManagementSystem;
    }

    public KerberosLoginStatus getStatus() {
        return status;
    }

    public void setStatus(KerberosLoginStatus status) {
        this.status = status;
    }
}
