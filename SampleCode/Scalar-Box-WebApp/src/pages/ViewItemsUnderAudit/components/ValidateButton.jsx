import { useSelector } from "react-redux";
import { BASE_URL } from "../../../utils/constants";
import { useState } from "react";
import { useEffect } from "react";
import LoadingButton from "@mui/lab/LoadingButton";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

export default function ValidateButton({ selectedItem, setValidate }) {
  const [isLoading, setIsloading] = useState(false);
  const axiosPrivate = useAxiosPrivate();

  const fetchAuditsetValidate = async () => {
    setIsloading(true);
    try {
      const response = await axiosPrivate.get(
        `${BASE_URL}/box/auditSet/validateAuditSet/${selectedItem.auditSetId}`
      );

      console.log("VALIDATE :: ", response);
      if (response.status === 200) {
        setValidate(response.data.data);
      } else {
        console.error("Failed to fetch user data:", response.statusText);
      }
    } catch (error) {
      console.error("Error fetching user data:", error);
    } finally {
      setIsloading(false);
    }
  };

  useEffect(() => {
    setValidate(null);
  }, [selectedItem]);

  return (
    <LoadingButton
      sx={{
        borderRadius: 28,
        textTransform: "none",
        main: "#0061D5",
        padding: "5px 20px",
      }}
      size="small"
      onClick={() => {
        if (selectedItem !== null) {
          fetchAuditsetValidate();
        }
      }}
      disabled={selectedItem === null}
      loading={isLoading}
      variant="contained"
    >
      <span>Validate</span>
    </LoadingButton>
  );

  // if (isLoading)
  //   return (
  //     <button
  //       className={`bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full ${
  //         selectedItem === null ? "opacity-50 cursor-not-allowed" : ""
  //       }`}
  //     >
  //       Validating ...
  //     </button>
  //   );

  // if (validate) {
  //   return <div>SUCCESS</div>;
  // }

  // return (
  //   <button
  //     className={`bg-[#0061D5] hover:bg-blue-800 text-white font-bold py-2 px-4 rounded-full ${
  //       selectedItem === null ? "opacity-50 cursor-not-allowed" : ""
  //     }`}
  //     // onClick={handleValidate}
  //     disabled={selectedItem === null}
  //     onClick={() => {
  //       if (selectedItem !== null) {
  //         fetchAuditsetValidate();
  //       }
  //     }}
  //   >
  //     Validate
  //   </button>
  // );
}
