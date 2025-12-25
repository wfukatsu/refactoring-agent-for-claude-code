import { Button } from "@mui/material";
import { CLIENT_ID, REDIRECT_URL } from "../utils/constant";
import "./SignUp.css";
import logo from "../assets/logo.png";

export default function SignUp({ userId, itemId, itemType }) {
  

  const handleOAuthButtonClick = (userId, itemId, itemType) => {
    const authorizationUrl = `https://account.box.com/api/oauth2/authorize?state=${userId},${itemId},${itemType}&response_type=code&client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URL}`;

    window.open(authorizationUrl, "_blank");

    window.close();
  };

  return (
    <div className="main_container">
      {/* <img
        className="absolute top-0 -z-10"
        alt="Rectangle"
        src="https://cdn.animaapp.com/projects/65b9ec8e286a69d30c95000e/releases/65b9ecc24ae9e26d265a26e6/img/rectangle-1.png"
      /> */}
      {/* <Header /> */}
      <div className="left_side_container ">
        <div>
          <img src={logo} alt="log" className="left_inner" />
          <p className="paregraph_left">File Auditing Tool by Scaler Inc.</p>
          <p className="inner_paregrapg">
            File Auditing Tool integrated with cloud storage services - Perform
            <br />
            auditing as per your convenience.
          </p>
        </div>
        <div>
          <div className="right_main_container">
            <div className="organisation_sidebar">
              <div className="organisation_inner">Sign In</div>
              <p className="welcome_text">
                Hello , Welcome back to our account !
              </p>
              <Button
                variant="outlined"
                className="SignUpBox_Button"
                onClick={() => handleOAuthButtonClick(userId, itemId, itemType)}
              >
                Sign Up as a Box User
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
