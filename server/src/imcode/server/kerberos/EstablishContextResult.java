package imcode.server.kerberos;


class EstablishContextResult {
    private boolean established;
    private byte[] spnegoResponseToken;
    private String clientPrincipalName;

    
    public EstablishContextResult() {
    }

    
    public String getClientPrincipalName() {
        return clientPrincipalName;
    }

    public void setClientPrincipalName(String clientPrincipalName) {
        this.clientPrincipalName = clientPrincipalName;
    }

    public boolean isEstablished() {
        return established;
    }

    public void setEstablished(boolean established) {
        this.established = established;
    }

    public byte[] getSpnegoResponseToken() {
        return spnegoResponseToken;
    }

    public void setSpnegoResponseToken(byte[] spnegoResponseToken) {
        this.spnegoResponseToken = spnegoResponseToken;
    }
}
