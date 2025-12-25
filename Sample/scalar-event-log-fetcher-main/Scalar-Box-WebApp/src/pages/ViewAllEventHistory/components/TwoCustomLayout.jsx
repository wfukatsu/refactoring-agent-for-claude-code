

import PickerWithButtonField from "./DatePicker";
import React from "react";

import { useTranslation } from "react-i18next";

const TwoCustomLayout = ({
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
          <option value="">{t("manageUserRoleScreenSelectText")}</option>
          {dropdownList.map((optionObject, index) => (
            <option
              className="dropdown-css-options"
              key={`option${index + 1}`}
              value={optionObject.userId} // Set userId as the value
              // value={optionObject} 
            >
              {optionObject.name}
              {/* {optionObject} */}
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
          <option value="">{t("manageUserRoleScreenSelectText")}</option>
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

export default TwoCustomLayout;
