package horizon.taglib.security;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
class AccountCredentials {

    private String name;
    private String password;

}