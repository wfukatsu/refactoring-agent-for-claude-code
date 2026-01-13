import React, { useEffect, useState } from "react";
import { ReactComponent as MySVG } from "../images/image 1.svg";
import { ReactComponent as MySettinglogo } from "../images/settings_FILL0_wght300_GRAD0_opsz24 1.svg";

import { ReactComponent as Notificationlogo } from "../images/notifications_FILL0_wght300_GRAD0_opsz24 1.svg";
import { ReactComponent as UpperArrow } from "../images/Vector.svg";
import { ReactComponent as Down } from "../images/Down.svg";
import Profile from "../images/Ellipse 3.png";
import ScalarLogo from "../images/ic_scalar.png";

function Header() {
  function setting() {
    alert("Setting");
  }
  function Notification() {
    alert("Notification");
  }

  return (
    <div
      style={{
        width: "100%",
        backgroundColor: "#00132B",
        top: "0",
      
      }}
    >
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
          alignItems: "center",
          padding: "10px",
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            alignItems: "center",
            paddingLeft: "9px",
          }}
        >
          <img
            src={ScalarLogo}
            style={{ width: "30px", height: "30px", marginRight: "10px" }}
          ></img>
          <div
            className="scalarheading"
            style={{
              backgroundColor: "#00132B",
              color: "white",
              fontSize: "21px",
              fontWeight: 900,
            }}
          >
            Scalar
          </div>

          <div style={{ width: "60px" }}></div>
        </div>

        <div
          style={{
            display: "flex",
            flexDirection: "row",
            alignItems: "center",
            gap: "40px",
          }}
        >
          <MySettinglogo
            style={{ width: "16px", height: "16px" }}
            onMouseEnter={(e) => {
              e.target.style.width = "18px";
              e.target.style.height = "18px";
            }}
            onMouseLeave={(e) => {
              e.target.style.width = "16px";
              e.target.style.height = "16px";
            }}
            onClick={setting}
          />
          <Notificationlogo
            onMouseEnter={(e) => {
              e.target.style.width = "18px";
              e.target.style.height = "18px";
            }}
            onMouseLeave={(e) => {
              e.target.style.width = "16px";
              e.target.style.height = "16px";
            }}
            onClick={Notification}
          />
          <div
            style={{
              display: "flex",
              flexDirection: "row",
              alignItems: "center",
              gap: "6px",
            }}
          >
            <img
              src={Profile}
              alt="Profile"
              style={{ width: "40px", height: "40px", borderRadius: "50%" }}
            />
            <div
              style={{
                display: "flex",
                flexDirection: "column",
                color: "white",
                marginLeft: "10px",
              }}
            >
              <div>Brayan Devidson</div>
              <div style={{ opacity: "0.6" }}>Auditor (EY)</div>
            </div>
            <div style={{ width: "5px" }}></div>
            <Down></Down>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Header;
