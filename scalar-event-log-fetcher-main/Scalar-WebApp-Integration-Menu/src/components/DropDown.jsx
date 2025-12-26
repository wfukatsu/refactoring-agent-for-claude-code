import { useDispatch, useSelector } from "react-redux";
import "../css/dropdown.css";
import { useEffect } from "react";
import { BASE_URL } from "../utils/constant";
import {
  updateAuditSet,
  updateSelectedAuditSet,
} from "../features/auditfolder/auditFolderSlice";
import useAxiosPrivate from "../hooks/useAxiosPrivate";

import { useTranslation } from "react-i18next";

export default function DropDown({ itemId }) {
  const { user } = useSelector((store) => store.auth);
  const { selectedAuditSet, auditSetList } = useSelector(
    (store) => store.folder
  );
  const axiosPrivate = useAxiosPrivate();
  const {t,i18n} = useTranslation();

  const dispatch = useDispatch();

  useEffect(() => {
    async function fetchAuditSet() {
      if (auditSetList.length === 0) {
        // const headers = new Headers();
        // headers.append("Content-Type", "application/json");
        // headers.append(
        //   "Authorization",
        //   `Bearer ${encodeURIComponent(user.jwtToken)}`
        // );

        const url = `${BASE_URL}/box/auditSet/getMyAuditSetList`;
        const response = await axiosPrivate.get(url);
        // const response = await fetch(url, {
        //   method: "GET",
        //   headers: headers,
        // });

        // console.log("response ", response.data.data);

        if (response.status === 200) {
          let auditSetList = [...response.data.data];
          dispatch(updateAuditSet(auditSetList));

          // console.log("audit list",auditSetList);
        }
      }
    }

    fetchAuditSet();
  }, [auditSetList.length, dispatch]);

  async function fetchAuditSetDeniList(selectedAuditSet) {
    if (selectedAuditSet === "null") {
      let allowedList = [];
      dispatch(updateSelectedAuditSet(allowedList, selectedAuditSet));
      return;
    }

    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append(
      "Authorization",
      `Bearer ${encodeURIComponent(user.jwtToken)}`
    );

    const url = `${BASE_URL}/box/auditSetItem/getAllowListFromAuditSet/${encodeURIComponent(
      selectedAuditSet
    )}/${encodeURIComponent(itemId)}`;
    // const response = await fetch(url, {
    //   method: "GET",
    //   headers: headers,
    // });

    const response = await axiosPrivate.get(url);

    // console.log("asdfa",response);

    if (response.status === 200) {
      // const jsonData = await response.json();
      let allowedList = [...response.data.data];
      dispatch(updateSelectedAuditSet(allowedList, selectedAuditSet));
    }
  }

  return (
    <div className="custom-lable-layout-container-five" data-content={t("rightClickSecondSelectAuditSetLabel")}>
      <select
        className="dropdown-css"
        style={{ paddingLeft: 10 }}
        onChange={async (event) => {
          const selectedAuditSet = event.target.value;
          await fetchAuditSetDeniList(selectedAuditSet);
        }}
        value={selectedAuditSet}
      >
        <option value="null">{t("rightClickPopUpTableSelectText")}</option>
        {[...auditSetList].map((auditSet, index) => (
          <option
            style={{ fontSize: "16px" }}
            className="dropdown-css-options"
            key={index}
            value={auditSet.auditSetId} // Set userId as the value
          >
            {auditSet.auditSetName}
          </option>
        ))}
      </select>
    </div>
  );
}
