import { Button } from "@mui/material";
import logo from "../../assets/logo.png";
import { SCALAR_BOX_URL } from "../../utils/constant";

import { useTranslation } from "react-i18next";

export default function LogoutPage() {
  const openNewWindow = () => {
    // Close the current window

    // window.location.href = "https://test7.jeeni.in/scalar-box";
    window.close();

    window.open(SCALAR_BOX_URL, "_blank");
  };

  const { t, i18n } = useTranslation();

  return (
    <div className="main_container">
      {/* <img
            className="absolute top-0 -z-10"
            alt="Rectangle"
            src="https://cdn.animaapp.com/projects/65b9ec8e286a69d30c95000e/releases/65b9ecc24ae9e26d265a26e6/img/rectangle-1.png"
          /> */}
      {/* <Header /> */}
      <div className="left_side_container">
        <div className="m-20">
          <img src={logo} alt="log" className="left_inner" />
          <p className="paregraph_left">{t("loginScreenTitleText")}</p>
          <p className="inner_paregrapg">
            {t("loginScreenInfoText")}
          </p>
        </div>
        {/* <div> */}
        <div className="right_main_container">
          <div style={{
            padding: "20px",
            display: "flex",
            flexDirection: "column",
            flex: "1"
          }}>

            <div style={{height:"60px"}}></div>

            <div
              style={{
                fontWeight: "bold",
                fontSize: "22px",
                paddingBottom: "60px",
                textAlign: "center"
              }}
            >
              {t("rightClickSucessLogOutText")}
            </div>


            <Button
              variant="outlined"
              onClick={openNewWindow}
            >
              {t("rightClickMainRedirectText")}
            </Button>
          </div>
          {/* </div> */}
        </div>
      </div>
    </div>
  );
}
