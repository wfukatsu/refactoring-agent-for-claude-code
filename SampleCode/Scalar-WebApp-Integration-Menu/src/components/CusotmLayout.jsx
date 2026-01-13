/* eslint-disable react/prop-types */
// import { ReactComponent as CalanderSVG } from "../svgicons/calendar_today_FILL1_wght300_GRAD0_opsz24 1.svg";
// import { ReactComponent as DropdownSVG } from "../svgicons/close_FILL1_wght300_GRAD0_opsz24 (1) 1.svg";

import PickerWithButtonField from "./DataPicker";
import { useTranslation } from "react-i18next";

const CustomLayout = ({
  firstList,
  typeContainer,
  value,
  setValue,
  dropdownList,
  dropdownListTwo,
  selectedOption,
  onDropdownChange,
}) => {
  const handleDropdownChange = (event) => {
    const selectedValue = event.target.value;
    onDropdownChange(selectedValue);
  };

  const {t,i18n} = useTranslation();

  return (
    <div className="custom-lable-row">
      {typeContainer === "DatePicker" ? (
        <PickerWithButtonField value={value} setValue={setValue} />
      ) : firstList ? (
        <select
          className="dropdown-css"
          style={{ paddingLeft: 10 }}
          onChange={(event) => handleDropdownChange(event)}
          value={selectedOption}
        >
          <option value="">{t("rightClickPopUpTableSelectText")}</option>
          {dropdownList.map((optionObject, index) => (
            <option
              className="dropdown-css-options"
              key={`option${index + 1}`}
              value={optionObject.userId} // Set userId as the value
            >
              {optionObject.name}
            </option>
          ))}
        </select>
      ) : (
        <select
          className="dropdown-css"
          style={{ paddingLeft: 10 }}
          onChange={(event) => handleDropdownChange(event)}
          value={selectedOption}
        >
          <option value="">{t("rightClickPopUpTableSelectText")}</option>
          {dropdownListTwo.map((eventType, index) => (
            <option
              className="dropdown-css-options"
              key={`eventType${index + 1}`}
              value={eventType} // Use the event type as the value
            >
              {eventType}
            </option>
          ))}
        </select>
      )}
      {typeContainer === "DatePicker" ? (
        <div></div>
      ) : (
        <div style={{ paddingRight: 10 }} />
      )}
    </div>
  );
};

export default CustomLayout;
