/* eslint-disable react/prop-types */
import CloseIcon from "../Icons/CloseIcon";
import { useTranslation } from "react-i18next";

const ClearButton = ({ onClick }) => {
  const handleClick = () => {
    if (onClick) {
      onClick(); // Invoke the provided callback function
    }
  };

  const {t,i18n} = useTranslation();

  const containerStyle = {
    width: "120px",
    height: "30px",
    display: "flex",
    alignItems: "center",
    cursor: "pointer",
  };

  return (
    <div style={containerStyle} onClick={handleClick}>
      <CloseIcon />
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
