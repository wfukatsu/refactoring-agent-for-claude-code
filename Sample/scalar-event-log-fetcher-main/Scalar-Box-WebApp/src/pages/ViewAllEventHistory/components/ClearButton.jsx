import React from "react";

import CloseIcon from "../../../assets/CloseIconSvg";

import { useTranslation } from "react-i18next";


const ClearButton = ({ onClick }) => {
  const handleClick = () => {
    if (onClick) {
      onClick(); // Invoke the provided callback function
    }
  };

  const containerStyle = {
    width: "90px",
    height: "30px",
    display: "flex",
    alignItems: "center",
    cursor: "pointer",
  };

  const {t,i18n} = useTranslation();

  return (
    <div style={containerStyle} onClick={handleClick}>
      <CloseIcon/>
      <button
        style={{
          width: 120,
          cursor: "pointer",
          border: "none",
          backgroundColor: "white",
          whiteSpace: "nowrap"
        }}
      >
        {t("viewAllEventHistoryScreenClearAllButtonText")}
      </button>
    </div>
  );
};

export default ClearButton;
